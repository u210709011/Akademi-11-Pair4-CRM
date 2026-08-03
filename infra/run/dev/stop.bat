@echo off
setlocal enabledelayedexpansion

rem Kills every java.exe process running one of our services (matched by its
rem own module directory appearing in the process command line, e.g.
rem "...\back-end\config-server\..."). NOT window-title based - start.bat
rem launches services as Windows Terminal TABS (wt new-tab), and tabs don't
rem have independent native window handles the way separate "start"-spawned
rem windows did, so taskkill's WINDOWTITLE filter can't reliably target one
rem specific tab's process. Matching the command line works regardless of
rem whether the process ended up in a tab, a separate window, or headless.
rem Pass "infra" as an argument to also bring down the docker/podman compose stack:
rem   stop.bat infra

set NAMES=config-server discovery-server api-gateway customer-service party-service contact-info-service order-service lookup-service product-service

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
