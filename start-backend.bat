@echo off
chcp 65001 >nul
echo ═══════════════════════════════════════════════════
echo   Khởi chạy Backend - Hệ thống Trợ cấp Xã hội
echo ═══════════════════════════════════════════════════
echo.

cd /d "%~dp0backend"

REM Kiểm tra Java đã cài chưa
java --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Chưa cài Java JDK!
    echo    Tải tại: https://jdk.java.net/25/
    echo    Cần cài JDK 25+ và thêm JAVA_HOME vào biến môi trường.
    pause
    exit /b 1
)

echo [1/2] Build project (dùng Maven Wrapper - không cần cài Maven)...
call mvnw.cmd clean package -DskipTests -q
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Build thất bại! Xem log lỗi ở trên.
    pause
    exit /b 1
)

echo [2/2] Khởi chạy server trên port 8080...
echo.
java -jar target\tro-cap-backend-1.0.0.jar
pause
