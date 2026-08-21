@echo off
rem Shared by every start.bat/stop.bat under infra\run\*. Picks Docker if it's
rem installed AND the daemon is actually reachable (most people already have
rem Docker Desktop), otherwise falls back to Podman. Callers `call` this file
rem so the variables below land in their own environment, then use %ENGINE%
rem for direct CLI commands (e.g. "%ENGINE% logs ...") and %COMPOSE% for
rem compose commands (e.g. "%COMPOSE% -f docker-compose.yml up -d").
rem
rem Bazi makinelerde birden fazla engine PATH'te bulunuyor ve "docker version"
rem yanit veriyor olsa bile (orn. Rancher Desktop'in kendi docker shim'i) o
rem calisan/veri iceren engine olmayabilir - bu durumda otomatik tespit yanlis
rem engine'i secip "container'larim kayboldu" sasirmasina yol aciyor (bir kez
rem basimiza geldi). CONTAINER_ENGINE ortam degiskeni ile bu tespiti tamamen
rem atlayip zorla secim yapilabilir, orn: "set CONTAINER_ENGINE=podman".

if defined CONTAINER_ENGINE (
    if /i "%CONTAINER_ENGINE%"=="podman" (
        set ENGINE=podman
        set COMPOSE=podman compose
        exit /b 0
    )
    if /i "%CONTAINER_ENGINE%"=="docker" (
        set ENGINE=docker
        set COMPOSE=docker compose
        exit /b 0
    )
    echo ERROR: CONTAINER_ENGINE="%CONTAINER_ENGINE%" - beklenen "docker" veya "podman".
    exit /b 1
)

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
