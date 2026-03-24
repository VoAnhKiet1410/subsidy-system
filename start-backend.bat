@echo off
chcp 65001 >nul
echo ═══════════════════════════════════════════════════
echo   Khởi chạy Backend - Hệ thống Trợ cấp Xã hội
echo ═══════════════════════════════════════════════════
echo.

cd /d "%~dp0backend"

echo [1/2] Build project (clean)...
call mvn clean package -DskipTests -q
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Build thất bại! Xem log lỗi ở trên.
    pause
    exit /b 1
)

echo [2/2] Khởi chạy server trên port 8080...
echo.
java -jar target\tro-cap-backend-1.0.0.jar
pause
