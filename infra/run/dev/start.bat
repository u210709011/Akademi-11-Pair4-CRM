@echo off
rem Re-launch with stdin explicitly attached to NUL. Code Runner (and some other
rem automation contexts) invoke this script with a stdin handle that cmd.exe's
rem "start" and piped commands (netstat | findstr) can't work with, failing with
rem "Input redirection is not supported, exiting the process immediately." A
rem clean re-launch with <nul fixes it for the whole process tree below it.
if "%~1"=="__RELAUNCHED__" goto :main
cmd /c "%~f0" __RELAUNCHED__ <nul
exit /b %errorlevel%

:main
setlocal enabledelayedexpansion

rem Starts the full CRM stack for local dev: infra pods (Postgres/Kafka/Redis/
rem Keycloak) via podman compose, then each Spring Boot service via a real,
rem vendored Apache Maven (downloaded once into back-end\.maven\ on first run).
rem NOT the project's mvnw.cmd wrapper - that script re-invokes itself via
rem powershell internally (see mvnw.cmd's own polyglot batch/powershell body),
rem which is blocked outright by Group Policy on some machines. Real Maven's
rem own mvn.cmd has zero PowerShell dependency, so this works everywhere.
rem Each service gets its own Windows Terminal tab (titled "CRM-<name>") in
rem one shared window, via "wt new-tab" - NOT separate "start"-spawned windows,
rem which some automation contexts (e.g. Code Runner) prevent from ever
rem becoming visible at all even though the underlying process does start.
rem stop.bat finds and kills these by matching each java.exe's own command
rem line (its module directory), not by window/tab title - tabs don't expose
rem an independent native window handle the way separate windows did, so
rem title-based matching can't reliably target one specific tab's process.
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

echo === Starting infra (Postgres, Kafka, Redis, Keycloak) ===
pushd "%INFRA%"
podman compose -f docker-compose.yml up -d postgres kafka kafka-ui debezium debezium-connectors redis redis-commander keycloak
popd

call :waitforport 5432 "PostgreSQL"
call :waitforport 9092 "Kafka"
call :waitforport 8180 "Keycloak"

echo === Building shared-contracts / shared-events (installed into local repo) ===
pushd "%BACKEND%"
"%MVN%" -pl shared-contracts,shared-events -am install -DskipTests -q <nul
if errorlevel 1 (
    echo ERROR: Failed to build shared-contracts/shared-events - see output above.
    popd
    exit /b 1
)
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
echo All services launched, each in its own "CRM-<name>" Windows Terminal tab. Check:
echo   Eureka:   http://localhost:8761
echo   Gateway:  http://localhost:8080/swagger-ui.html
echo   Keycloak: http://localhost:8180 (admin/admin)
echo.
echo To stop everything: stop.bat
exit /b 0

:startwindow
echo Launching %~1...
wt -w 0 new-tab --title "CRM-%~1" cmd /k "cd /d "%BACKEND%\%~1" && "%MVN%" spring-boot:run"
exit /b 0

:waitforport
set PORT=%~1
set LABEL=%~2
set COUNT=0
echo Waiting for %LABEL% on port %PORT%...
:waitloop
netstat -an <nul | findstr /R /C:":%PORT% .*LISTENING" >nul
if not errorlevel 1 (
    echo %LABEL% is up.
    exit /b 0
)
set /a COUNT+=1
if %COUNT% GEQ 60 (
    echo WARNING: %LABEL% did not come up on port %PORT% after 2 minutes - continuing anyway.
    exit /b 1
)
timeout /t 2 /nobreak >nul
goto waitloop
