@echo off
rem Stops and removes all containers in the stack. Pass "clean" to also drop
rem the Postgres/Kafka data volumes (full reset):  stop.bat clean
set INFRA=%~dp0..\..
for %%I in ("%INFRA%") do set INFRA=%%~fI
cd /d "%INFRA%"

call "%~dp0..\detect-engine.bat"
if errorlevel 1 exit /b 1

if /I "%~1"=="clean" (
    %COMPOSE% -f docker-compose.yml down -v
) else (
    %COMPOSE% -f docker-compose.yml down
)
