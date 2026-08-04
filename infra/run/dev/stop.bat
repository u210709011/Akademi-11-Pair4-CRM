@echo off
setlocal enabledelayedexpansion

rem Backend services: auto-kill via taskkill turned out fundamentally
rem unreliable on this project's machines when run from inside ANY batch
rem script - the exact same "taskkill /PID X /T /F" reliably succeeds as a
rem one-off standalone command, and just as reliably fails ("not found")
rem when run from a script's for loop, no matter what's being targeted
rem (java.exe, cmd.exe), how it's matched, or how long a delay is added
rem beforehand. Root cause never pinned down (a plausible guess: AV/EDR
rem software intercepting process termination specifically when it
rem originates from a script). So instead of pretending this works, this
rem just reports which services are actually running (matching java.exe by
rem module directory IS reliable - only the kill is not) and tells you to
rem close their windows yourself (Ctrl+C or the X button - 100% reliable).
rem
rem Front-end is the one exception - killing whatever's listening on port
rem 4200 (not the wmic-spawned process tree) has proven reliable even from
rem within these scripts, so that one still auto-stops.
rem
rem Pass a service name (or "front-end") to target ONLY that one:
rem   stop.bat product-service
rem   stop.bat front-end
rem Pass "infra" as an argument to also bring down the docker/podman compose stack:
rem   stop.bat infra
rem No argument checks all 9 services + stops front-end (infra keeps running).

set NAMES=config-server discovery-server api-gateway customer-service party-service contact-info-service order-service lookup-service product-service
set STOP_FRONTEND=1
if not "%~1"=="" if /I not "%~1"=="infra" (
    set NAMES=%~1
    set STOP_FRONTEND=0
    if /I "%~1"=="front-end" set STOP_FRONTEND=1
)

set "ANY_RUNNING="
set "CURRENT_CMD="
for /f "usebackq delims=" %%L in (`wmic process where "name='java.exe'" get CommandLine^,ProcessId /format:list`) do (
    set "LINE=%%L"
    if "!LINE:~0,12!"=="CommandLine=" (
        set "CURRENT_CMD=!LINE:~12!"
    )
    if "!LINE:~0,10!"=="ProcessId=" (
        set "CURRENT_PID=!LINE:~10!"
        for %%N in (%NAMES%) do (
            echo !CURRENT_CMD! | findstr /C:"\back-end\%%N\" >nul
            if not errorlevel 1 (
                echo %%N is running ^(PID !CURRENT_PID!^) - close its window to stop it
                set "ANY_RUNNING=1"
            )
        )
        set "CURRENT_CMD="
    )
)
if not defined ANY_RUNNING if "%STOP_FRONTEND%"=="0" echo %NAMES% wasn't running.

if "%STOP_FRONTEND%"=="1" call :killfrontend

if /I "%~1"=="infra" (
    set INFRA=%~dp0..\..
    for %%I in ("%INFRA%") do set INFRA=%%~fI
    call "%~dp0..\detect-engine.bat"
    if errorlevel 1 exit /b 1
    echo === Stopping infra ===
    pushd "%INFRA%"
    %COMPOSE% -f docker-compose.yml down
    popd
)

exit /b 0

:killfrontend
rem Finds whatever process is actually LISTENING on port 4200 (netstat) and
rem kills it directly, rather than matching the wmic-spawned top-level
rem cmd.exe by command line (tried first - wmic reliably found the right PID
rem but taskkill just as reliably reported "not found" for it, same as the
rem backend services above). Killing the actual listening process directly
rem is grounded in reality (it IS the thing holding the port) and works
rem reliably; its parent chain (cmd/npm/npx wrappers) exits on its own once
rem its child is gone, tearing down the whole tree without needing to
rem explicitly target it (verified).
set "FOUND="
for /f "tokens=5" %%P in ('netstat -ano ^| findstr /R /C:":4200 .*LISTENING"') do (
    if not "%%P"=="0" (
        set "FOUND=1"
        taskkill /PID %%P /T /F >nul 2>&1
    )
)
if defined FOUND (echo Stopped front-end) else (echo Front-end wasn't running)
exit /b 0
