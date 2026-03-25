@echo off
chcp 65001 > nul
echo ============================================
echo   IMPORT DU LIEU MAU - He Thong Tro Cap
echo ============================================
echo.

REM Tìm mongorestore trong các vị trí phổ biến
SET MONGORESTORE=mongorestore
IF EXIST "C:\Program Files\MongoDB\Tools\100\bin\mongorestore.exe" (
    SET MONGORESTORE=C:\Program Files\MongoDB\Tools\100\bin\mongorestore.exe
)
IF EXIST "C:\Program Files\MongoDB\Server\7.0\bin\mongorestore.exe" (
    SET MONGORESTORE=C:\Program Files\MongoDB\Server\7.0\bin\mongorestore.exe
)
IF EXIST "C:\Program Files\MongoDB\Server\6.0\bin\mongorestore.exe" (
    SET MONGORESTORE=C:\Program Files\MongoDB\Server\6.0\bin\mongorestore.exe
)

echo [1/2] Dang xoa database cu (neu co)...
"%MONGORESTORE%" --drop --db trocap "dump\trocap" --quiet

IF %ERRORLEVEL% NEQ 0 (
    echo.
    echo [LOI] Import that bai!
    echo Kiem tra: MongoDB dang chay? mongorestore da duoc cai dat?
    echo Download MongoDB Database Tools tai: https://www.mongodb.com/try/download/database-tools
    pause
    exit /b 1
)

echo [2/2] Import hoan tat!
echo.
echo ============================================
echo   Du lieu da duoc nhap thanh cong!
echo   - Users: admin / officer1 / accountant1 / citizen1
echo   - Password mac dinh: 123456
echo ============================================
echo.
pause
