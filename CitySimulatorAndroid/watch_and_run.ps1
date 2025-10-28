param(
    [string]$Port = "5555",
    [string]$Variant = "Debug"
)

$ErrorActionPreference = 'Stop'

$projRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$runScript = Join-Path $projRoot 'run_ldplayer.ps1'
if (-not (Test-Path $runScript)) { throw "run_ldplayer.ps1 not found" }

# Paths to watch (you can add more)
$paths = @(
    Join-Path $projRoot 'app\src',
    Join-Path $projRoot 'build.gradle',
    Join-Path $projRoot 'settings.gradle',
    Join-Path $projRoot 'app\build.gradle'
) | Where-Object { Test-Path $_ }

Write-Host "Watching for changes... (Ctrl+C to stop)" -ForegroundColor Cyan

$lastRun = Get-Date 0
$debounce = [TimeSpan]::FromSeconds(2)

$watchers = @()
foreach ($p in $paths) {
    $fsw = New-Object System.IO.FileSystemWatcher $p -Property @{IncludeSubdirectories=$true;EnableRaisingEvents=$true}
    $handlers = @('Changed','Created','Deleted','Renamed')
    foreach ($h in $handlers) {
        Register-ObjectEvent $fsw $h -SourceIdentifier ("FSW_"+$h+"_"+$p) -Action {
            $script:lastEvent = Get-Date
        } | Out-Null
    }
    $watchers += $fsw
}

# Initial build
& $runScript -Port $Port -Variant $Variant
$lastEvent = Get-Date

while ($true) {
    Start-Sleep -Seconds 1
    if ($lastEvent -and ((Get-Date) - $lastEvent) -gt $debounce -and $lastEvent -gt $lastRun) {
        try {
            Write-Host "Change detected, rebuilding..." -ForegroundColor Yellow
            & $runScript -Port $Port -Variant $Variant
            $lastRun = Get-Date
        } catch {
            Write-Warning $_
        }
        $lastEvent = $null
    }
}
