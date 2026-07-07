@echo off
setlocal

cd /d %~dp0

rem Compile
call build.bat
if %errorlevel% neq 0 exit /b %errorlevel%

rem Run (main class)
java -cp "build;lib/*" main.Main
endlocal


