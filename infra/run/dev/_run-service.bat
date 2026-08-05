@echo off
rem Internal helper for start.bat - NOT meant to be run directly. Launched
rem via "wmic process call create", which spawns through the WMI provider
rem host and does NOT inherit the calling shell's environment variables -
rem so this computes its own paths from %~dp0 (same trick start.bat itself
rem uses) instead of relying on inherited BACKEND/MVN paths. Only argument
rem is the service name, kept deliberately simple: every attempt at passing
rem multiple quoted path arguments through nested shells in this project has
rem turned out fragile (mis-parses into "Access is denied", a stray "Windows
rem cannot find '\\'" dialog, or silently nothing launching at all).
rem
rem wmic's process creation always opens a new console window (no "no
rem window" flag like "start /B" has) - since a window shows up either way,
rem this lets Maven's own output print directly into it instead of hiding it
rem behind a redirect the window would never display, and titles it so the 9
rem windows are distinguishable.
set SERVICE=%~1
title CRM dev - %SERVICE%

set BACKEND=%~dp0..\..\..\back-end
for %%I in ("%BACKEND%") do set BACKEND=%%~fI
set MVN=%BACKEND%\.maven\apache-maven-3.9.16\bin\mvn.cmd

cd /d "%BACKEND%\%SERVICE%"
call "%MVN%" spring-boot:run
