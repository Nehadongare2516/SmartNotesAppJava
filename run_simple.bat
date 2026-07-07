@echo off
setlocal
cd /d %~dp0

rem Always compile
call build.bat
if %errorlevel% neq 0 exit /b %errorlevel%

rem Windows cmd.exe requires semicolon classpath without quoting the whole -cp string
java -cp build;lib/* main.Main
endlocal

