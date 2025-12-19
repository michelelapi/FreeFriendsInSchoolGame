# PowerShell script to build Android App Bundle (AAB) for Google Play Store

Write-Host "Building Android App Bundle (AAB) for Google Play..." -ForegroundColor Green
Write-Host ""

# Check if keystore.properties exists
if (-not (Test-Path "keystore.properties")) {
    Write-Host "ERROR: keystore.properties not found!" -ForegroundColor Red
    Write-Host "Please run create-keystore.ps1 first to create your signing key." -ForegroundColor Yellow
    exit 1
}

# Check if keystore file exists
$keystoreProps = @{}
Get-Content "keystore.properties" | ForEach-Object {
    if ($_ -match '^\s*([^#][^=]+)=(.*)$') {
        $key = $matches[1].Trim()
        $value = $matches[2].Trim()
        $keystoreProps[$key] = $value
    }
}

$keystorePath = $keystoreProps["storeFile"]
if ([string]::IsNullOrWhiteSpace($keystorePath)) {
    Write-Host "ERROR: storeFile not found in keystore.properties!" -ForegroundColor Red
    Write-Host "Please check your keystore.properties file." -ForegroundColor Yellow
    exit 1
}

# Handle relative paths
if ($keystorePath.StartsWith("../")) {
    $keystorePath = $keystorePath.Substring(3)
}

if (-not (Test-Path $keystorePath)) {
    Write-Host "ERROR: Keystore file not found at: $keystorePath" -ForegroundColor Red
    Write-Host "Please check your keystore.properties file." -ForegroundColor Yellow
    exit 1
}

Write-Host "Building release AAB..." -ForegroundColor Cyan
Write-Host ""

# Build the release AAB
try {
    if (Test-Path "gradlew.bat") {
        & .\gradlew.bat bundleRelease
    } else {
        & .\gradlew bundleRelease
    }
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "Build successful!" -ForegroundColor Green
        Write-Host ""
        
        $aabPath = "app\build\outputs\bundle\release\app-release.aab"
        if (Test-Path $aabPath) {
            $fileInfo = Get-Item $aabPath
            Write-Host "AAB file location: $aabPath" -ForegroundColor Cyan
            Write-Host "File size: $([math]::Round($fileInfo.Length / 1MB, 2)) MB" -ForegroundColor Cyan
            Write-Host ""
            Write-Host "Next steps:" -ForegroundColor Yellow
            Write-Host "1. Go to Google Play Console (https://play.google.com/console)" -ForegroundColor White
            Write-Host "2. Create a new app or select existing app" -ForegroundColor White
            Write-Host "3. Go to Production -> Create new release" -ForegroundColor White
            Write-Host "4. Upload the AAB file: $aabPath" -ForegroundColor White
            Write-Host ""
        } else {
            Write-Host "WARNING: AAB file not found at expected location!" -ForegroundColor Yellow
        }
    } else {
        Write-Host "Build failed! Check the error messages above." -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "ERROR: Build failed: $_" -ForegroundColor Red
    exit 1
}

