# Script to rename the project folder
# This preserves all files including Cursor chat history (which is stored outside the project)

param(
    [Parameter(Mandatory=$true)]
    [string]$NewFolderName
)

$CurrentPath = $PSScriptRoot
$ParentPath = Split-Path -Parent $CurrentPath
$CurrentFolderName = Split-Path -Leaf $CurrentPath
$NewPath = Join-Path $ParentPath $NewFolderName

Write-Host "Current folder: $CurrentPath" -ForegroundColor Cyan
Write-Host "New folder: $NewPath" -ForegroundColor Cyan
Write-Host ""

# Check if new folder already exists
if (Test-Path $NewPath) {
    Write-Host "ERROR: The folder '$NewFolderName' already exists!" -ForegroundColor Red
    exit 1
}

# Confirm with user
$confirmation = Read-Host "Are you sure you want to rename '$CurrentFolderName' to '$NewFolderName'? (yes/no)"
if ($confirmation -ne "yes") {
    Write-Host "Operation cancelled." -ForegroundColor Yellow
    exit 0
}

Write-Host "Renaming folder..." -ForegroundColor Green

try {
    # Close any open files/editors first (user should do this manually)
    Write-Host "Please close Cursor/Android Studio before proceeding..." -ForegroundColor Yellow
    Start-Sleep -Seconds 3
    
    # Rename the folder
    Rename-Item -Path $CurrentPath -NewName $NewFolderName -ErrorAction Stop
    
    Write-Host "Folder renamed successfully!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Next steps:" -ForegroundColor Cyan
    Write-Host "1. Open the renamed folder in Cursor" -ForegroundColor White
    Write-Host "2. Update settings.gradle.kts with the new project name" -ForegroundColor White
    Write-Host "3. Your Cursor chat history is preserved (stored outside the project folder)" -ForegroundColor White
    Write-Host ""
    Write-Host "New project path: $NewPath" -ForegroundColor Green
    
} catch {
    Write-Host "ERROR: Failed to rename folder: $_" -ForegroundColor Red
    exit 1
}

