@echo off
setlocal
cd /d %~dp0

rem Compile only (no dependency on mysql)
call build.bat
if %errorlevel% neq 0 exit /b %errorlevel%

rem Run (uses JSON local storage via modified DAOs)
java -cp "build;lib/*" main.Main
endlocal

