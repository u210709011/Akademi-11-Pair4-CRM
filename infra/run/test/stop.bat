@echo off
rem Stops and removes all containers in the stack. Pass "clean" to also drop
rem the Postgres/Kafka data volumes (full reset):  stop.bat clean
set INFRA=%~dp0..\..
for %%I in ("%INFRA%") do set INFRA=%%~fI
cd /d "%INFRA%"
if /I "%~1"=="clean" (
    podman compose -f docker-compose.yml down -v
) else (
    podman compose -f docker-compose.yml down
)
