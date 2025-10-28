@echo off
setlocal
cd /d %~dp0
powershell -NoProfile -ExecutionPolicy Bypass -File "scripts\auto_build_install.ps1" 5555 Debug
endlocal
