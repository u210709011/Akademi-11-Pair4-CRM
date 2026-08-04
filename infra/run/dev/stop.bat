@echo off
setlocal enabledelayedexpansion

rem Kills every java.exe process running one of our services (matched by its
rem own module directory appearing in the process command line, e.g.
rem "...\back-end\config-server\..."). NOT window-title based - matching the
rem command line works regardless of how the process was launched or what,
rem if anything, its window is titled.
rem Pass a service name to stop ONLY that one:  stop.bat product-service
rem Pass "infra" as an argument to also bring down the docker/podman compose stack:
rem   stop.bat infra
rem No argument stops all 9 services (infra keeps running).

set NAMES=config-server discovery-server api-gateway customer-service party-service contact-info-service order-service lookup-service product-service
if not "%~1"=="" if /I not "%~1"=="infra" set NAMES=%~1

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
                taskkill /PID !CURRENT_PID! /T /F >nul 2>&1
                if not errorlevel 1 echo Stopped %%N (PID !CURRENT_PID!)
            )
        )
        set "CURRENT_CMD="
    )
)

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
