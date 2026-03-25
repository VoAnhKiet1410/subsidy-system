@echo off
chcp 65001 >nul
echo ===================================================
echo   Khoi chay Backend - He thong Tro cap Xa hoi
echo ===================================================
echo.

cd /d "%~dp0backend"

REM Kiem tra Java da cai chua
java --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [LOI] Chua cai Java JDK!
    echo    Tai tai: https://jdk.java.net/25/
    echo    Can cai JDK 25+ va them JAVA_HOME vao bien moi truong.
    pause
    exit /b 1
)

REM Neu truyen tham so --build thi build lai tu source
if "%1"=="--build" (
    echo [BUILD] Build project tu source code...
    call mvnw.cmd clean package -DskipTests -q
    if %ERRORLEVEL% NEQ 0 (
        echo [LOI] Build that bai! Xem log loi o tren.
        pause
        exit /b 1
    )
    echo [OK] Build thanh cong! Cap nhat app.jar...
    copy /Y "target\tro-cap-backend-1.0.0.jar" "app.jar" >nul
)

echo [OK] Khoi chay server tren port 8080...
echo.
echo      Tip: start-backend.bat           = chay nhanh (khong build lai)
echo      Tip: start-backend.bat --build   = build tu source roi chay
echo.
java -jar app.jar
pause