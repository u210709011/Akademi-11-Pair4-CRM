@echo off
rem Builds (if needed) and starts the entire CRM stack with SPRING_PROFILE=test.
rem Infra is brought up first and separately - "up --build" on the whole file
rem builds every image (all 9 backend services + front-end) as one upfront
rem phase before starting ANY container, so postgres/keycloak/etc would
rem otherwise sit invisible for the whole build even though they need no
rem building at all. Starting infra on its own gives immediate visible
rem feedback while the slower app builds run.
set INFRA=%~dp0..\..
for %%I in ("%INFRA%") do set INFRA=%%~fI
cd /d "%INFRA%"

call "%~dp0..\detect-engine.bat"
if errorlevel 1 exit /b 1
echo Using container engine: %ENGINE%

set SPRING_PROFILE=test
echo Using SPRING_PROFILE=%SPRING_PROFILE%

echo === Starting infra (Postgres, Kafka, Redis, Keycloak, Grafana/Prometheus/Loki/Tempo) ===
%COMPOSE% -f docker-compose.yml up -d postgres kafka kafka-ui debezium debezium-connectors redis redis-commander keycloak prometheus loki tempo grafana

echo.
echo === Building and starting app services + front-end ===
%COMPOSE% -f docker-compose.yml up -d --build
echo.
echo Stack starting. Check:
echo   Eureka:    http://localhost:8761
echo   Gateway:   http://localhost:8080/swagger-ui.html
echo   Frontend:  http://localhost:4200
echo   Keycloak:  http://localhost:8180 (admin/admin)
echo   Kafka UI:  http://localhost:8090
echo   Debezium:  http://localhost:8083/connectors
echo   Redis UI:  http://localhost:8081
echo   Grafana:   http://localhost:3000
echo.
echo Tail logs with: %COMPOSE% logs -f ^<service^>
echo Stop everything with: stop.bat
