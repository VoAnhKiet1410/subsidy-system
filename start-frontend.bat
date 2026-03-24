@echo off
chcp 65001 >nul
echo ===================================================
echo   Khoi chay Frontend - He thong Tro cap Xa hoi
echo ===================================================
echo.

cd /d "%~dp0frontend"
npm run dev
pause