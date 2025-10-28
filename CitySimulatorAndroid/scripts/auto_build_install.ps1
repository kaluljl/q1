Param(
  [string]$LdPort = "5555",
  [string]$BuildType = "Debug"
)

$ErrorActionPreference = "Stop"
$root = Split-Path $PSScriptRoot -Parent
$proj = Join-Path $root "app"
$apkPath = Join-Path $proj "build\outputs\apk\$($BuildType.ToLower())\app-$($BuildType.ToLower()).apk"
$gradlew = Join-Path $root "gradlew.bat"
$packageName = "com.citysimulator.game"

function Find-AdbPath {
  $candidates = @(
    "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe",
    "C:\Android\platform-tools\adb.exe",
    "D:\AndroidSDK\platform-tools\adb.exe",
    "D:\Android\Sdk\platform-tools\adb.exe",
    "C:\Program Files\Android\platform-tools\adb.exe"
  )
  foreach ($p in $candidates) { if (Test-Path $p) { return $p } }
  $which = (Get-Command adb -ErrorAction SilentlyContinue)
  if ($which) { return $which.Source }
  throw "adb not found. Please install Android platform-tools or add to PATH."
}

function Invoke-GradleBuild {
  Write-Host "==> Building APK ($BuildType)" -ForegroundColor Cyan
  & $gradlew "assemble$BuildType" --no-daemon | Write-Output
  if ($LASTEXITCODE -ne 0) { throw "Gradle build failed (exit $LASTEXITCODE)" }
  if (-not (Test-Path $apkPath)) { throw "APK not found at $apkPath" }
  Write-Host "==> APK ready: $apkPath" -ForegroundColor Green
}

function Connect-LDPlayer([string]$adb, [string]$port) {
  Write-Host "==> Connecting LDPlayer adb at 127.0.0.1:$port" -ForegroundColor Cyan
  & $adb connect "127.0.0.1:$port" | Write-Output
}

function Get-DeviceSerial([string]$adb, [string]$port) {
  $target = "127.0.0.1:$port"
  $list = & $adb devices | Select-String -Pattern $target
  if (-not $list) { throw "Target device $target not found in 'adb devices'" }
  return $target
}

function Install-And-Run([string]$adb, [string]$serial) {
  Write-Host "==> Installing APK to $serial" -ForegroundColor Cyan
  & $adb -s $serial install -r "$apkPath" | Write-Output
  Write-Host "==> Launching app $packageName on $serial" -ForegroundColor Cyan
  & $adb -s $serial shell monkey -p $packageName -c android.intent.category.LAUNCHER 1 | Write-Output
}

$adbPath = Find-AdbPath
Invoke-GradleBuild

# Try preferred port, then fallback
try {
  Connect-LDPlayer -adb $adbPath -port $LdPort
  $serial = Get-DeviceSerial -adb $adbPath -port $LdPort
  Install-And-Run -adb $adbPath -serial $serial
} catch {
  Write-Warning $_
  Write-Host "==> Fallback to port 62001" -ForegroundColor Yellow
  Connect-LDPlayer -adb $adbPath -port "62001"
  $serial = Get-DeviceSerial -adb $adbPath -port "62001"
  Install-And-Run -adb $adbPath -serial $serial
}
