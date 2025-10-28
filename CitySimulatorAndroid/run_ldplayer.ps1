param(
    [string]$Port = "5555",        # LDPlayer ADB port; change for multi-instances
    [string]$Variant = "Debug"     # Build variant: Debug/Release
)

$ErrorActionPreference = 'Stop'

function Get-AdbPath {
    # Allow environment override first
    $sdkDir = $env:SDKDIR

    # Try local.properties if not provided
    if (-not $sdkDir) {
        $projRoot = $PSScriptRoot
        $localProps = Join-Path $projRoot 'local.properties'
        if (Test-Path $localProps) {
            $match = Select-String -Path $localProps -Pattern '^sdk\.dir\s*=\s*(.+)$' -ErrorAction SilentlyContinue
            if ($match) { $sdkDir = ($match.Line -replace '^sdk\.dir\s*=\s*', '') }
        }
    }
    if (-not $sdkDir) { $sdkDir = 'D:\AndroidSdk' }

    # Normalize path: remove quotes and spaces
    $sdkDir = $sdkDir.Trim().Trim('"')

    $adb = Join-Path $sdkDir 'platform-tools\adb.exe'
    if (-not (Test-Path $adb)) { throw "ADB not found at: $adb. Set SDKDIR env var or update local.properties sdk.dir" }
    return $adb
}

function Invoke-Gradle {
    param([string]$Args)
    $projRoot = $PSScriptRoot
    Push-Location $projRoot
    try {
        Write-Host "==> Running Gradle: $Args" -ForegroundColor Cyan
        $wrapperJar = Join-Path $projRoot 'gradle\wrapper\gradle-wrapper.jar'
        if (Test-Path $wrapperJar) {
            & .\gradlew.bat $Args
        } else {
            # fallback to system gradle
            $gradleCmd = (Get-Command gradle -ErrorAction SilentlyContinue)
            if (-not $gradleCmd) { throw "Gradle wrapper jar missing and system 'gradle' not found in PATH. Please install Gradle 8.2.1 or restore wrapper jar." }
            & gradle $Args
        }
    } finally { Pop-Location }
}

$adb = Get-AdbPath

# 1) Build APK
$task = if ($Variant -ieq 'Release') { 'assembleRelease' } else { 'assembleDebug' }
Invoke-Gradle $task

$projRoot = $PSScriptRoot
$apk = if ($Variant -ieq 'Release') {
    Join-Path $projRoot 'app\build\outputs\apk\release\app-release.apk'
} else {
    Join-Path $projRoot 'app\build\outputs\apk\debug\app-debug.apk'
}
if (-not (Test-Path $apk)) { throw "APK not found: $apk" }

# 2) Connect to LDPlayer
Write-Host "==> Connecting to LDPlayer at 127.0.0.1:$Port" -ForegroundColor Cyan
& $adb kill-server | Out-Null
& $adb connect 127.0.0.1:$Port | Out-Null

# Check device state
$devices = & $adb devices
if ($devices -notmatch "127\.0\.0\.1:$Port\s+device") {
    throw "Failed to connect to LDPlayer at 127.0.0.1:$Port. Ensure ADB debugging is enabled and port is correct."
}

# 3) Install APK
Write-Host "==> Installing APK: $apk" -ForegroundColor Cyan
& $adb install -r "$apk" 2>$null
if ($LASTEXITCODE -ne 0) {
    # try uninstall then install
    & $adb uninstall com.citysimulator.game | Out-Null
    & $adb install -r "$apk"
}

# 4) Launch app
$appId = 'com.citysimulator.game'
Write-Host "==> Launching $appId" -ForegroundColor Cyan
& $adb shell monkey -p $appId -c android.intent.category.LAUNCHER 1 | Out-Null

Write-Host "==> Done." -ForegroundColor Green
