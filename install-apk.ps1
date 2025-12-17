# Script to install APK via ADB
# Make sure your phone is connected via USB and USB debugging is enabled

$apkPath = "app\build\outputs\apk\debug\app-debug.apk"

Write-Host "Checking ADB connection..."
adb devices

Write-Host "`nInstalling APK..."
adb install -r $apkPath

if ($LASTEXITCODE -eq 0) {
    Write-Host "`nAPK installed successfully!"
}
else {
    Write-Host "`nInstallation failed. Error code: $LASTEXITCODE"
    Write-Host "`nTroubleshooting tips:"
    Write-Host "1. Make sure USB debugging is enabled on your phone"
    Write-Host "2. Check that your phone is connected and authorized"
    Write-Host "3. Try: adb kill-server; adb start-server"
    Write-Host '4. Make sure your Android version is 7.0 or higher (API 24+)'
}

