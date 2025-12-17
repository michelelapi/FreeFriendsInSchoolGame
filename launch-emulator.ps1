# Android Emulator Launcher Script
# This script helps launch Android emulators from VS Code

$sdkPath = $env:ANDROID_HOME
if (-not $sdkPath) {
    $sdkPath = "$env:LOCALAPPDATA\Android\Sdk"
}

$emulatorPath = "$sdkPath\emulator\emulator.exe"

if (-not (Test-Path $emulatorPath)) {
    Write-Host "Error: Android emulator not found at: $emulatorPath" -ForegroundColor Red
    Write-Host "Please install Android Studio and create an AVD first." -ForegroundColor Yellow
    Write-Host "See EMULATOR_SETUP.md for instructions." -ForegroundColor Yellow
    exit 1
}

# List available AVDs
Write-Host "`nAvailable Android Virtual Devices:" -ForegroundColor Cyan
$avds = & $emulatorPath -list-avds

if ($avds.Count -eq 0) {
    Write-Host "No AVDs found. Please create one in Android Studio:" -ForegroundColor Yellow
    Write-Host "  Tools → Device Manager → Create Device" -ForegroundColor Yellow
    exit 1
}

# Display AVDs
for ($i = 0; $i -lt $avds.Count; $i++) {
    Write-Host "  [$i] $($avds[$i])" -ForegroundColor Green
}

# Prompt for selection
Write-Host "`nSelect an AVD to launch (0-$($avds.Count - 1)) or press Enter for first one:" -ForegroundColor Cyan
$selection = Read-Host

if ([string]::IsNullOrWhiteSpace($selection)) {
    $avdName = $avds[0]
} else {
    $index = [int]$selection
    if ($index -ge 0 -and $index -lt $avds.Count) {
        $avdName = $avds[$index]
    } else {
        Write-Host "Invalid selection. Launching first AVD." -ForegroundColor Yellow
        $avdName = $avds[0]
    }
}

Write-Host "`nLaunching emulator: $avdName" -ForegroundColor Green
Write-Host "This may take a minute. The emulator window will open shortly..." -ForegroundColor Yellow

# Launch emulator in background
Start-Process -FilePath $emulatorPath -ArgumentList "-avd", $avdName

Write-Host "`nEmulator is starting. Wait for it to fully boot, then run:" -ForegroundColor Cyan
Write-Host "  .\gradlew installDebug" -ForegroundColor White
Write-Host "`nTo check if emulator is ready, run:" -ForegroundColor Cyan
Write-Host "  adb devices" -ForegroundColor White

