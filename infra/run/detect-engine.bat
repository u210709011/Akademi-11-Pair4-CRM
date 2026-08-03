@echo off
rem Shared by every start.bat/stop.bat under infra\run\*. Picks Docker if it's
rem installed AND the daemon is actually reachable (most people already have
rem Docker Desktop), otherwise falls back to Podman. Callers `call` this file
rem so the variables below land in their own environment, then use %ENGINE%
rem for direct CLI commands (e.g. "%ENGINE% logs ...") and %COMPOSE% for
rem compose commands (e.g. "%COMPOSE% -f docker-compose.yml up -d").

where docker >nul 2>&1
if not errorlevel 1 (
    docker version >nul 2>&1
    if not errorlevel 1 (
        set ENGINE=docker
        set COMPOSE=docker compose
        exit /b 0
    )
)

where podman >nul 2>&1
if not errorlevel 1 (
    set ENGINE=podman
    set COMPOSE=podman compose
    exit /b 0
)

echo ERROR: Neither Docker nor Podman was found (or reachable) on this machine.
echo Install Docker Desktop or Podman and make sure it's running, then retry.
exit /b 1
