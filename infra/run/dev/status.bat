@echo off
rem Reports what's actually up from a dev-mode start.bat run.
rem Fixed-port infra (Postgres/Kafka/Keycloak/config-server/discovery-server/
rem api-gateway) is checked by port. The 6 business services get a random
rem port each run (server.port: ${PORT:0}), so port-checking them is useless -
rem instead this checks (a) whether a matching java.exe process exists (same
rem command-line match stop.bat uses) and (b) whether Eureka actually has it
rem registered, which is the real "is it usable" signal.
rem
rem Auto-refreshes every 5s by default (Ctrl+C to stop) instead of a one-shot
rem snapshot - pass "once" for a single check instead: status.bat once
setlocal enabledelayedexpansion

if /I "%~1"=="once" goto :report

:loop
cls
echo === CRM dev stack status - refreshing every 5s, Ctrl+C to stop ===
echo.
call :report
timeout /t 5 /nobreak >nul
goto :loop

:report
echo === Infra (fixed ports) ===
call :checkport 5432 PostgreSQL
call :checkport 9092 Kafka
call :checkport 8180 Keycloak
call :checkport 8888 "Config Server"
call :checkport 8761 "Discovery Server / Eureka"
call :checkport 8080 "API Gateway"

echo.
echo === Business services (dynamic ports - process + Eureka registration) ===
set NAMES=customer-service party-service contact-info-service order-service lookup-service product-service

for %%N in (%NAMES%) do (
    set "FOUND="
    for /f "usebackq delims=" %%L in (`wmic process where "name='java.exe'" get CommandLine /format:list 2^>nul`) do (
        set "LINE=%%L"
        echo !LINE! | findstr /C:"\back-end\%%N\" >nul
        if not errorlevel 1 set "FOUND=1"
    )
    if defined FOUND (set "PROC=running") else (set "PROC=NOT running")

    set "EUREKA_NAME=%%N"
    call :toupper EUREKA_NAME
    curl -s -o nul -w "%%{http_code}" http://localhost:8761/eureka/apps/!EUREKA_NAME! > "%TEMP%\eureka_check.tmp" 2>nul
    set /p HTTP_CODE=<"%TEMP%\eureka_check.tmp"
    if "!HTTP_CODE!"=="200" (set "EUR=registered in Eureka") else (set "EUR=NOT registered in Eureka")

    echo   %%N: !PROC!, !EUR!
)
del "%TEMP%\eureka_check.tmp" >nul 2>&1

exit /b 0

:checkport
set PORT=%~1
set LABEL=%~2
netstat -an | findstr /R /C:":%PORT% .*LISTENING" >nul
if not errorlevel 1 (
    echo   %LABEL% ^(port %PORT%^): UP
) else (
    echo   %LABEL% ^(port %PORT%^): DOWN
)
exit /b 0

:toupper
for %%A in ("a=A" "b=B" "c=C" "d=D" "e=E" "f=F" "g=G" "h=H" "i=I" "j=J" "k=K" "l=L" "m=M" "n=N" "o=O" "p=P" "q=Q" "r=R" "s=S" "t=T" "u=U" "v=V" "w=W" "x=X" "y=Y" "z=Z" "-=-") do (
    for /f "tokens=1,2 delims==" %%X in (%%A) do call set "%~1=%%%~1:%%X=%%Y%%"
)
exit /b 0
