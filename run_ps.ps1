# Run script for Windows PowerShell
# Usage: powershell -ExecutionPolicy Bypass -File .\run_ps.ps1

$ErrorActionPreference = 'Stop'

$base = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $base

# Compile
& .\build.bat
if ($LASTEXITCODE -ne 0) { throw "build.bat failed with exit code $LASTEXITCODE" }

# Run (classpath uses ';' for Windows)
$cp = "build;lib/*"
java -cp $cp main.Main

