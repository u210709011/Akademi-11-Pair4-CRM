@echo off
rem Restarts ONE service without touching the rest of the stack. Use this
rem after fixing code in a service that crashed or that you just edited.
rem Usage: restart.bat <service-name>
rem   e.g. restart.bat product-service
setlocal enabledelayedexpansion

if "%~1"=="" (
    echo Usage: restart.bat ^<service-name^>
    echo   e.g. restart.bat product-service
    echo Valid names: config-server discovery-server api-gateway customer-service
    echo              party-service contact-info-service order-service lookup-service
    echo              product-service
    exit /b 1
)
set SERVICE=%~1

echo Stopping %SERVICE% if it's running...
set "FOUND="
set "CURRENT_CMD="
for /f "usebackq delims=" %%L in (`wmic process where "name='java.exe'" get CommandLine^,ProcessId /format:list`) do (
    set "LINE=%%L"
    if "!LINE:~0,12!"=="CommandLine=" set "CURRENT_CMD=!LINE:~12!"
    if "!LINE:~0,10!"=="ProcessId=" (
        set "CURRENT_PID=!LINE:~10!"
        echo !CURRENT_CMD! | findstr /C:"\back-end\%SERVICE%\" >nul
        if not errorlevel 1 (
            taskkill /PID !CURRENT_PID! /T /F >nul 2>&1
            if not errorlevel 1 (
                echo   Stopped %SERVICE% ^(PID !CURRENT_PID!^)
                set "FOUND=1"
            )
        )
        set "CURRENT_CMD="
    )
)
if not defined FOUND echo   ^(wasn't running^)

echo Launching %SERVICE% ^(a new titled window will open, live output there^)...
wmic process call create "cmd /c call %~dp0_run-service.bat %SERVICE%" >nul

echo Done. Check its window for live output, or run status.bat
