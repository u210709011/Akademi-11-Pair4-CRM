@echo off
rem Internal helper for start.bat - NOT meant to be run directly. Launched
rem via "wmic process call create", same as _run-service.bat, but runs the
rem Angular front-end instead of a backend service via Maven. Uses "npx ng
rem serve" rather than a global "ng" install (unreliable in practice on this
rem project's machines) or "npm start" (works, but npx is the team's normal
rem workflow here, so this matches what everyone actually runs by hand).
rem Installs node_modules once if missing, same idea as start.bat vendoring
rem Maven on first run.
title CRM dev - front-end

set FRONTEND=%~dp0..\..\..\front-end
for %%I in ("%FRONTEND%") do set FRONTEND=%%~fI

cd /d "%FRONTEND%"
if not exist "%FRONTEND%\node_modules" (
    echo node_modules not found - running npm install once ^(this can take a while^)...
    call npm install
)
call npx ng serve
