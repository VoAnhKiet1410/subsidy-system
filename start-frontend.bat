@echo off
chcp 65001 >nul
echo ═══════════════════════════════════════════════════
echo   Khởi chạy Frontend - Hệ thống Trợ cấp Xã hội
echo ═══════════════════════════════════════════════════
echo.

cd /d "%~dp0frontend"
npm run dev
pause
