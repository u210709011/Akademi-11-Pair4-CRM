@echo off
rem No stdin relaunch trick here anymore - it forced NUL onto stdin for the
rem *entire* process tree below it (originally to survive Code Runner's
rem broken stdin handle), and that turned out to also break things run under
rem it (Maven dying silently mid-build with zero error output, for one).
rem Run this from a real terminal (cmd/PowerShell/Windows Terminal) - it
rem doesn't work reliably from Code Runner regardless, since Code Runner's
rem execution context can't keep background processes alive at all, which no
rem stdin trick fixes anyway.
setlocal enabledelayedexpansion

rem Starts the full CRM stack for local dev: infra pods (Postgres/Kafka/Redis/
rem Keycloak) via docker/podman compose (whichever is installed), then each
rem Spring Boot service via a real, vendored Apache Maven (downloaded once
rem into back-end\.maven\ on first run).
rem NOT the project's mvnw.cmd wrapper - that script re-invokes itself via
rem powershell internally (see mvnw.cmd's own polyglot batch/powershell body),
rem which is blocked outright by Group Policy on some machines. Real Maven's
rem own mvn.cmd has zero PowerShell dependency, so this works everywhere.
rem Each service is spawned via "wmic process call create" (see :startwindow
rem below) - unlike "wt new-tab" (silently produced nothing under some
rem execution contexts) or "start /B" (unreliable, inconsistent failures
rem depending on quoting), wmic's process creation doesn't depend on any
rem console/window station at all, so it survives regardless of how this
rem script itself was launched. wmic always opens its own console window for
rem the spawned process (no "hidden" option like "start /B" has), so Maven's
rem live output just prints straight into that window instead of being
rem redirected anywhere - see _run-service.bat, which titles each window
rem "CRM dev - <service>".
rem stop.bat/restart.bat find and kill these by matching each java.exe's own
rem command line (its module directory), not by window title.
rem Services default to the "dev" profile on their own (each service's local
rem application.yml: SPRING_PROFILES_ACTIVE:dev) - no override needed here.
rem
rem Each service is run from its OWN directory (like mvnw.cmd originally was),
rem NOT via "-pl <module> -am spring-boot:run" from the reactor root - that
rem form runs the spring-boot:run goal against EVERY reactor member being
rem built (shared-contracts, shared-events, ...), and fails immediately since
rem those library modules have no main class. Instead the shared modules are
rem installed into the local repo ONCE up front (a normal "install" phase,
rem which doesn't try to run anything), then each service resolves them as
rem ordinary dependencies from ~/.m2, exactly like mvnw.cmd used to.

set INFRA=%~dp0..\..
for %%I in ("%INFRA%") do set INFRA=%%~fI
set ROOT=%INFRA%\..
for %%I in ("%ROOT%") do set ROOT=%%~fI
set BACKEND=%ROOT%\back-end

set MAVEN_DIR=%BACKEND%\.maven
set MVN=%MAVEN_DIR%\apache-maven-3.9.16\bin\mvn.cmd

if not exist "%MVN%" (
    echo === Maven not found locally - downloading Apache Maven 3.9.16 once ===
    if not exist "%MAVEN_DIR%" mkdir "%MAVEN_DIR%"
    curl -fsSL -o "%MAVEN_DIR%\maven.zip" https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.16/apache-maven-3.9.16-bin.zip
    if not exist "%MAVEN_DIR%\maven.zip" (
        echo ERROR: Maven download failed - check your internet connection and retry.
        exit /b 1
    )
    tar -xf "%MAVEN_DIR%\maven.zip" -C "%MAVEN_DIR%"
    del "%MAVEN_DIR%\maven.zip"
    echo Maven ready at %MVN%
)

call "%~dp0..\detect-engine.bat"
if errorlevel 1 exit /b 1
echo Using container engine: %ENGINE%

echo === Starting infra (Postgres, Kafka, Redis, Keycloak) ===
pushd "%INFRA%"
%COMPOSE% -f docker-compose.yml up -d postgres kafka kafka-ui debezium debezium-connectors redis redis-commander keycloak
popd

call :waitforport 5432 "PostgreSQL"
call :waitforport 9092 "Kafka"
call :waitforport 8180 "Keycloak"

echo === Building shared-contracts / shared-events (installed into local repo) ===
pushd "%BACKEND%"
call "%MVN%" -pl shared-contracts,shared-events -am install -DskipTests -q
if errorlevel 1 (
    echo ERROR: Failed to build shared-contracts/shared-events - see output above.
    popd
    exit /b 1
)
echo shared-contracts/shared-events built OK ^(-q means Maven prints nothing on success^).
popd

echo === Starting Config Server ===
call :startwindow config-server
call :waitforport 8888 "Config Server"

echo === Starting Discovery Server (Eureka) ===
call :startwindow discovery-server
call :waitforport 8761 "Discovery Server"

echo === Starting API Gateway + business services ===
call :startwindow api-gateway
timeout /t 2 /nobreak >nul
call :startwindow customer-service
timeout /t 2 /nobreak >nul
call :startwindow party-service
timeout /t 2 /nobreak >nul
call :startwindow contact-info-service
timeout /t 2 /nobreak >nul
call :startwindow order-service
timeout /t 2 /nobreak >nul
call :startwindow lookup-service
timeout /t 2 /nobreak >nul
call :startwindow product-service

echo.
echo All services launched, each in its own titled window (live Maven output there).
echo   Eureka:   http://localhost:8761
echo   Gateway:  http://localhost:8080/swagger-ui.html
echo   Keycloak: http://localhost:8180 (admin/admin)
echo.
echo status.bat            - check what's up
echo restart.bat ^<service^> - restart just one
echo stop.bat / stop.bat ^<service^> - stop everything / stop just one
exit /b 0

:startwindow
rem "wmic process call create" spawns a fully detached process via WMI -
rem unlike "start /B", this reliably survives regardless of how THIS script
rem itself was launched (real terminal, Code Runner, etc.), since it isn't
rem tied to any console/window station at all. It does NOT inherit this
rem shell's environment variables though (WMI provider host runs separately),
rem hence _run-service.bat computing its own paths from %~dp0. Its one string
rem argument goes through wmic's OWN command-line parsing, which does not
rem reliably support nested quotes in testing - deliberately left unquoted
rem here (works because this repo's own path has no spaces in it; if you
rem move this project under a path containing spaces, this will need
rem revisiting).
echo Launching %~1 ...
wmic process call create "cmd /c call %~dp0_run-service.bat %~1" >nul
exit /b 0

:waitforport
set PORT=%~1
set LABEL=%~2
set COUNT=0
echo Waiting for %LABEL% on port %PORT% (prints a dot every 10s so this doesn't look stuck)...
:waitloop
netstat -an | findstr /R /C:":%PORT% .*LISTENING" >nul
if not errorlevel 1 (
    echo %LABEL% is up.
    exit /b 0
)
set /a COUNT+=1
set /a MOD=COUNT %% 5
set /a ELAPSED=COUNT*2
if %MOD%==0 echo   ...still waiting for %LABEL% ^(%ELAPSED%s^)
if %COUNT% GEQ 60 (
    echo WARNING: %LABEL% did not come up on port %PORT% after 2 minutes - continuing anyway.
    exit /b 1
)
timeout /t 2 /nobreak >nul
goto waitloop
