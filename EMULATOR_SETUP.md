# Android Emulator Setup for VS Code

VS Code doesn't have a built-in Android emulator, but you can use Android Studio's emulator from VS Code. Here are your options:

## Option 1: Install Android Studio (Recommended)

1. **Download and Install Android Studio**
   - Download from: https://developer.android.com/studio
   - During installation, make sure to install:
     - Android SDK
     - Android SDK Platform
     - Android Virtual Device (AVD)

2. **Create an Android Virtual Device (AVD)**
   - Open Android Studio
   - Go to **Tools → Device Manager**
   - Click **Create Device**
   - Select a device (e.g., Pixel 5)
   - Select a system image (e.g., API 34 - Android 14)
   - Finish the setup

3. **Add Emulator to PATH** (Optional but recommended)
   
   Add the emulator directory to your system PATH:
   ```powershell
   # Default location (adjust if different)
   $emulatorPath = "$env:LOCALAPPDATA\Android\Sdk\emulator"
   $env:Path += ";$emulatorPath"
   
   # To make it permanent (run as Administrator):
   [System.Environment]::SetEnvironmentVariable("Path", $env:Path + ";$emulatorPath", "User")
   ```

## Option 2: VS Code Extensions

Install these extensions in VS Code:

1. **Android Emulator Launcher** (`adelphes.android-dev-ext`)
   - Search for "Android Emulator Launcher" in VS Code Extensions
   - Allows you to launch emulators directly from VS Code

2. **AVD Manager** (optional)
   - Provides a GUI to manage AVDs from VS Code

## Option 3: Command Line (After Setup)

Once Android Studio is installed and you've created an AVD:

### List available emulators:
```powershell
# If emulator is in PATH:
emulator -list-avds

# Or use full path:
$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe -list-avds
```

### Launch an emulator:
```powershell
# Replace "Pixel_5_API_34" with your AVD name
emulator -avd Pixel_5_API_34

# Or use full path:
$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe -avd Pixel_5_API_34
```

### Launch emulator in background:
```powershell
Start-Process -FilePath "$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe" -ArgumentList "-avd", "Pixel_5_API_34"
```

## Using VS Code Tasks

After setting up Android Studio, you can use the VS Code tasks:

1. **Press `Ctrl+Shift+P`** → Type "Tasks: Run Task"
2. Select **"Android: List Emulators"** to see available AVDs
3. Select **"Android: Launch Emulator"** and enter the AVD name when prompted

## Verify Setup

1. **Check if emulator is accessible:**
   ```powershell
   adb devices
   ```

2. **After launching an emulator, verify it's connected:**
   ```powershell
   adb devices
   # Should show something like:
   # List of devices attached
   # emulator-5554   device
   ```

3. **Then install your app:**
   ```powershell
   .\gradlew installDebug
   ```

## Troubleshooting

### Emulator command not found
- Make sure Android Studio is installed
- Verify `ANDROID_HOME` environment variable is set
- Add emulator to PATH (see Option 1, step 3)

### Emulator won't start
- Ensure virtualization is enabled in BIOS (Intel VT-x or AMD-V)
- Check that Hyper-V is disabled (if using Windows)
- Try launching from Android Studio first to verify it works

### No AVDs found
- Open Android Studio → Device Manager
- Create a new AVD
- Make sure it's saved in the default location

## Quick Start Script

Create a PowerShell script `launch-emulator.ps1`:

```powershell
# List available AVDs
$avds = & "$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe" -list-avds
Write-Host "Available AVDs:"
$avds | ForEach-Object { Write-Host "  - $_" }

# Launch first AVD (or specify name)
if ($avds.Count -gt 0) {
    $avdName = $avds[0]
    Write-Host "`nLaunching: $avdName"
    Start-Process -FilePath "$env:LOCALAPPDATA\Android\Sdk\emulator\emulator.exe" -ArgumentList "-avd", $avdName
} else {
    Write-Host "No AVDs found. Please create one in Android Studio."
}
```

Run it with: `.\launch-emulator.ps1`

