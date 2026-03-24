@REM -------------------------------------------------------------------
@REM Maven Wrapper startup batch script
@REM -------------------------------------------------------------------
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM -------------------------------------------------------------------
@REM Fixed: Support Vietnamese/Unicode directory paths
@REM -------------------------------------------------------------------

@echo off
@setlocal EnableDelayedExpansion

chcp 65001 >nul 2>&1

set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

@REM Find project base dir
set "MAVEN_PROJECTBASEDIR=%~dp0"
if "%MAVEN_PROJECTBASEDIR:~-1%"=="\" set "MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%"

@REM Change to project dir first (avoids Unicode path issues with -classpath)
cd /d "%MAVEN_PROJECTBASEDIR%"

@REM Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome
set "JAVA_EXE=java.exe"
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto checkMavenWrapperJar
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH. >&2
echo Please set the JAVA_HOME variable in your environment to match the location of your Java installation. >&2
goto error
:findJavaFromJavaHome
set "JAVA_HOME=%JAVA_HOME:"=%"
set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
if exist "%JAVA_EXE%" goto checkMavenWrapperJar
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME% >&2
echo Please set the JAVA_HOME variable in your environment to match the location of your Java installation. >&2
goto error

:checkMavenWrapperJar
set "WRAPPER_JAR=.mvn\wrapper\maven-wrapper.jar"
if exist "%WRAPPER_JAR%" (
    @REM Using the wrapper jar (use relative path "." to avoid Unicode issues)
    "%JAVA_EXE%" %MAVEN_OPTS% ^
      -classpath "%WRAPPER_JAR%" ^
      "-Dmaven.multiModuleProjectDirectory=." ^
      %WRAPPER_LAUNCHER% %*
) else (
    @REM Fallback: download wrapper jar on-the-fly
    for /f "tokens=* USEBACKQ" %%a in (".mvn\wrapper\maven-wrapper.properties") do (
        set "%%a"
    )
    if not defined wrapperUrl (
        set "wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"
    )
    @REM Download wrapper jar using curl (better Unicode support than PowerShell)
    where curl >nul 2>&1
    if !ERRORLEVEL! == 0 (
        curl -sL -o "%WRAPPER_JAR%" "!wrapperUrl!"
    ) else (
        @REM Fallback to PowerShell
        powershell -Command "&{"^
		"$webclient = new-object System.Net.WebClient;"^
		"[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12;"^
		"$webclient.DownloadFile('!wrapperUrl!', '%WRAPPER_JAR%')"^
		"}"
    )
    @REM If download succeeded, run it
    if exist "%WRAPPER_JAR%" (
        "%JAVA_EXE%" %MAVEN_OPTS% ^
          -classpath "%WRAPPER_JAR%" ^
          "-Dmaven.multiModuleProjectDirectory=." ^
          %WRAPPER_LAUNCHER% %*
    ) else (
        @REM Ultimate fallback: use mvn from PATH
        echo Wrapper JAR download failed, attempting to use mvn from PATH...
        mvn %*
    )
)
if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%

cmd /C exit /B %ERROR_CODE%
