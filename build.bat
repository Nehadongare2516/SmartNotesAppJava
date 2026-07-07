@echo off
setlocal

cd /d %~dp0

if not exist build mkdir build

rem Compile all sources into build/ using optional libs in lib/
rem Windows-compatible compilation: use a file list from FOR /R.
setlocal EnableDelayedExpansion
set "SOURCES="
for /r src %%f in (*.java) do set "SOURCES=!SOURCES! "%%f""
javac -encoding UTF-8 -cp "lib/*" -d build !SOURCES!

endlocal


