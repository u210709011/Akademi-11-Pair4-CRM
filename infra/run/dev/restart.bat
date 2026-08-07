@echo off
rem Launches ONE service (or the front-end) without touching the rest of the
rem stack. Usage: restart.bat <service-name>
rem   e.g. restart.bat product-service
rem   e.g. restart.bat front-end
rem
rem For backend services: if it's already running, this REFUSES to launch a
rem second copy and asks you to close its window first (Ctrl+C or the X
rem button), rather than starting a duplicate - auto-killing turned out to
rem be fundamentally unreliable when run from inside these scripts on this
rem project's machines (see stop.bat for the full story), so rather than
rem silently doing nothing (or worse, launching a confusing duplicate), this
rem is upfront about needing you to close the window yourself.
rem
rem Front-end is the one exception - killing whatever's listening on port
rem 4200 has proven reliable even from within these scripts, so restarting
rem it (kill then relaunch) works automatically, same as before.
setlocal enabledelayedexpansion

if "%~1"=="" (
    echo Usage: restart.bat ^<service-name^>
    echo   e.g. restart.bat product-service
    echo Valid names: config-server discovery-server api-gateway customer-service
    echo              party-service contact-info-service order-service lookup-service
    echo              product-service front-end
    exit /b 1
)
set SERVICE=%~1

if /I "%SERVICE%"=="front-end" goto :restartfrontend

set "CURRENT_CMD="
for /f "usebackq delims=" %%L in (`wmic process where "name='java.exe'" get CommandLine^,ProcessId /format:list`) do (
    set "LINE=%%L"
    if "!LINE:~0,12!"=="CommandLine=" set "CURRENT_CMD=!LINE:~12!"
    if "!LINE:~0,10!"=="ProcessId=" (
        set "CURRENT_PID=!LINE:~10!"
        echo !CURRENT_CMD! | findstr /C:"\back-end\%SERVICE%\" >nul
        if not errorlevel 1 (
            echo %SERVICE% is already running ^(PID !CURRENT_PID!^).
            echo Close its window ^(Ctrl+C or the X button^), then run this again.
            exit /b 1
        )
        set "CURRENT_CMD="
    )
)

echo Launching %SERVICE% ^(a new titled window will open, live output there^)...
wmic process call create "cmd /c call %~dp0_run-service.bat %SERVICE%" >nul
echo Done. Check its window for live output, or run status.bat
exit /b 0

:restartfrontend
rem Kills whatever process is actually LISTENING on port 4200 (netstat)
rem rather than matching the wmic-spawned process tree by command line -
rem see stop.bat for why (wmic reliably found the right PID, taskkill just
rem as reliably couldn't kill it; killing the real listening process works
rem and its parent chain tears itself down once its child exits).
echo Stopping front-end if it's running...
set "FOUND="
for /f "tokens=5" %%P in ('netstat -ano ^| findstr /R /C:":4200 .*LISTENING"') do (
    if not "%%P"=="0" (
        set "FOUND=1"
        taskkill /PID %%P /T /F >nul 2>&1
    )
)
if defined FOUND (echo   Stopped front-end) else (echo   ^(wasn't running^))

echo Launching front-end ^(a new titled window will open, live output there^)...
wmic process call create "cmd /c call %~dp0_run-frontend.bat" >nul
echo Done. Check its window for live output, or run status.bat
exit /b 0
