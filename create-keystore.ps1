# PowerShell script to create a keystore for Android app signing
# This keystore is required for Google Play Store uploads

Write-Host "Creating Android Release Keystore..." -ForegroundColor Green
Write-Host ""

# Prompt for keystore details
$keystorePath = Read-Host "Enter keystore file path (default: schoolescape-release-key.jks)"
if ([string]::IsNullOrWhiteSpace($keystorePath)) {
    $keystorePath = "schoolescape-release-key.jks"
}

$keyAlias = Read-Host "Enter key alias (default: schoolescape-key)"
if ([string]::IsNullOrWhiteSpace($keyAlias)) {
    $keyAlias = "schoolescape-key"
}

$storePassword = Read-Host "Enter store password" -AsSecureString
$keyPassword = Read-Host "Enter key password (can be same as store password)" -AsSecureString

# Convert SecureString to plain text for keytool command
$storePasswordPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
    [Runtime.InteropServices.Marshal]::SecureStringToBSTR($storePassword)
)
$keyPasswordPlain = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
    [Runtime.InteropServices.Marshal]::SecureStringToBSTR($keyPassword)
)

# Check if keytool is available
$keytoolPath = $null
$javaHome = $env:JAVA_HOME
if ($javaHome) {
    $keytoolPath = Join-Path $javaHome "bin\keytool.exe"
    if (-not (Test-Path $keytoolPath)) {
        $keytoolPath = $null
    }
}

if (-not $keytoolPath) {
    # Try to find keytool in PATH
    $keytoolPath = Get-Command keytool -ErrorAction SilentlyContinue
    if ($keytoolPath) {
        $keytoolPath = $keytoolPath.Source
    }
}

if (-not $keytoolPath -or -not (Test-Path $keytoolPath)) {
    Write-Host "ERROR: keytool not found!" -ForegroundColor Red
    Write-Host "Please install Java JDK and ensure JAVA_HOME is set, or add keytool to your PATH." -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "Creating keystore at: $keystorePath" -ForegroundColor Cyan
Write-Host "Key alias: $keyAlias" -ForegroundColor Cyan
Write-Host ""

# Create keystore
$keytoolArgs = @(
    "-genkey",
    "-v",
    "-keystore", $keystorePath,
    "-alias", $keyAlias,
    "-keyalg", "RSA",
    "-keysize", "2048",
    "-validity", "10000",
    "-storepass", $storePasswordPlain,
    "-keypass", $keyPasswordPlain,
    "-dname", "CN=School Escape, OU=Development, O=Your Company, L=City, ST=State, C=US"
)

try {
    & $keytoolPath $keytoolArgs
    
    Write-Host ""
    Write-Host "Keystore created successfully!" -ForegroundColor Green
    Write-Host ""
    
    # Create keystore.properties file
    Write-Host "Creating keystore.properties file..." -ForegroundColor Cyan
    
    $keystoreProps = @"
storeFile=../$keystorePath
storePassword=$storePasswordPlain
keyAlias=$keyAlias
keyPassword=$keyPasswordPlain
"@
    
    $keystoreProps | Out-File -FilePath "keystore.properties" -Encoding UTF8
    
    Write-Host "keystore.properties created!" -ForegroundColor Green
    Write-Host ""
    Write-Host "IMPORTANT:" -ForegroundColor Yellow
    Write-Host "1. Keep your keystore file ($keystorePath) safe and backed up!" -ForegroundColor Yellow
    Write-Host "2. If you lose it, you won't be able to update your app on Google Play!" -ForegroundColor Yellow
    Write-Host "3. Add keystore.properties to .gitignore to keep it secure!" -ForegroundColor Yellow
    Write-Host ""
    
} catch {
    Write-Host "ERROR: Failed to create keystore: $_" -ForegroundColor Red
    exit 1
}

