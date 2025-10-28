@echo off
setlocal enabledelayedexpansion

REM ===== Parameters =====
set PORT=%1
if "%PORT%"=="" set PORT=5555
set VARIANT=%2
if /I "%VARIANT%"=="" set VARIANT=Debug

REM ===== Resolve SDK path from local.properties (fallback to D:\AndroidSdk) =====
set SDKDIR=D:\AndroidSdk
if exist local.properties (
  for /f "usebackq tokens=1,* delims==" %%A in ("local.properties") do (
    if /I "%%A"=="sdk.dir" set SDKDIR=%%B
  )
)
set ADB="%SDKDIR%\platform-tools\adb.exe"
if not exist %ADB% (
  echo ERROR: ADB not found at %ADB%
  echo Please install Android SDK or update local.properties sdk.dir
  exit /b 1
)

REM ===== Decide Gradle command (wrapper or system gradle) =====
set GRADLE_CMD=.\r
gradlew.bat
if exist gradle\wrapper\gradle-wrapper.jar (
  set GRADLE_CMD=.\gradlew.bat
) else (
  where gradle >nul 2>&1
  if errorlevel 1 (
    echo Gradle wrapper jar missing and system 'gradle' not found.
    echo Please install Gradle 8.2.1 and add it to PATH, or restore gradle\wrapper\gradle-wrapper.jar.
    exit /b 1
  ) else (
    set GRADLE_CMD=gradle
  )
)

REM ===== Build APK =====
if /I "%VARIANT%"=="Release" (
  set TASK=assembleRelease
  set APK=app\build\outputs\apk\release\app-release.apk
) else (
  set TASK=assembleDebug
  set APK=app\build\outputs\apk\debug\app-debug.apk
)

echo ==^> Running Gradle: %TASK% using %GRADLE_CMD%
call %GRADLE_CMD% %TASK%
if errorlevel 1 (
  echo Gradle build failed.
  exit /b 1
)
if not exist "%APK%" (
  echo APK not found: %APK%
  exit /b 1
)

REM ===== Connect LDPlayer =====
echo ==^> Connecting LDPlayer 127.0.0.1:%PORT%
%ADB% kill-server >nul 2>&1
%ADB% connect 127.0.0.1:%PORT% >nul
%ADB% devices | findstr /R /C:"127\.0\.0\.1:%PORT%[ ]*device" >nul
if errorlevel 1 (
  echo Failed to connect to LDPlayer at 127.0.0.1:%PORT%.
  echo Please enable ADB debugging in LDPlayer and confirm the port.
  exit /b 1
)

REM ===== Install APK =====
echo ==^> Installing %APK%
%ADB% install -r "%APK%"
if errorlevel 1 (
  echo Install failed. Trying uninstall old package...
  %ADB% uninstall com.citysimulator.game >nul 2>&1
  %ADB% install -r "%APK%"
  if errorlevel 1 (
    echo Install failed again.
    exit /b 1
  )
)

REM ===== Launch App =====
echo ==^> Launching com.citysimulator.game
%ADB% shell monkey -p com.citysimulator.game -c android.intent.category.LAUNCHER 1 >nul

echo ==^> Done.
exit /b 0
