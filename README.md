# Android Development Setup for VS Code

This workspace is configured for Android app development using VS Code.

## Prerequisites

Before you start, make sure you have the following installed:

### 1. Java Development Kit (JDK)

- **Required**: JDK 11 or higher (JDK 17 recommended)
- Download from: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptium.net/)
- Verify installation:
  ```powershell
  java -version
  ```

### 2. Android SDK

- **Required**: Android SDK (via Android Studio or standalone)
- Download Android Studio from: [developer.android.com/studio](https://developer.android.com/studio)
- Or install SDK Command Line Tools only

### 3. Set Environment Variables (Windows)

Add these to your system environment variables:

```powershell
# Set ANDROID_HOME (adjust path to your SDK location)
ANDROID_HOME=C:\Users\<YourUsername>\AppData\Local\Android\Sdk

# Add to PATH
%ANDROID_HOME%\platform-tools
%ANDROID_HOME%\tools
%ANDROID_HOME%\tools\bin
```

To set in PowerShell (run as Administrator):

```powershell
[System.Environment]::SetEnvironmentVariable("ANDROID_HOME", "C:\Users\<YourUsername>\AppData\Local\Android\Sdk", "User")
[System.Environment]::SetEnvironmentVariable("Path", $env:Path + ";%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\tools;%ANDROID_HOME%\tools\bin", "User")
```

### 4. Gradle

- Usually comes with Android projects via Gradle Wrapper (`gradlew`)
- **To install Gradle CLI separately** (if you want to use `gradle` command):

#### Option A: Using Gradle Wrapper (Recommended for Android Projects)

Android projects include a Gradle Wrapper (`gradlew` or `gradlew.bat`). Use this instead:

```powershell
.\gradlew build
.\gradlew clean
```

#### Option B: Install Gradle CLI Manually

1. Download Gradle from: [gradle.org/releases](https://gradle.org/releases/)
2. Extract to a folder (e.g., `C:\Gradle`)
3. Add to PATH:
   ```powershell
   # Run PowerShell as Administrator
   [System.Environment]::SetEnvironmentVariable("Path", $env:Path + ";C:\Gradle\gradle-8.x\bin", "User")
   ```
4. Set GRADLE_HOME:
   ```powershell
   [System.Environment]::SetEnvironmentVariable("GRADLE_HOME", "C:\Gradle\gradle-8.x", "User")
   ```
5. Restart VS Code/PowerShell and verify:
   ```powershell
   gradle -v
   ```

#### Option C: Using Package Manager (Chocolatey)

```powershell
# Install Chocolatey first (if not installed)
# Then run:
choco install gradle
```

## VS Code Setup

### Step 1: Install Recommended Extensions

When you open this workspace, VS Code will prompt you to install recommended extensions. Click "Install All" or install them manually:

1. **Extension Pack for Java** - Java language support
2. **Kotlin** - Kotlin language support
3. **Gradle for Java** - Gradle build tool support
4. **XML** - XML support (for AndroidManifest.xml, layouts)
5. **SonarLint** - Code quality checks

### Step 2: Configure Android SDK Path

Update `.vscode/settings.json` with your Android SDK path:

```json
"android.sdk.path": "C:\\Users\\<YourUsername>\\AppData\\Local\\Android\\Sdk"
```

Or ensure `ANDROID_HOME` environment variable is set correctly.

### Step 3: Verify Setup

1. Open VS Code in this workspace
2. Open a terminal (Ctrl + `)
3. Check Android SDK:
   ```powershell
   adb version
   ```
4. Check Java:
   ```powershell
   java -version
   ```

## Creating a New Android Project

### Option 1: Using Android Studio

1. Create a new project in Android Studio
2. Copy the project files to this workspace
3. Open this workspace in VS Code

### Option 2: Using Command Line

```powershell
# Install Android project template (if you have Android SDK tools)
# Or use Android Studio to create project, then open in VS Code
```

### Option 3: Using Gradle Init

```powershell
gradle init --type java-application
# Then modify for Android structure
```

## Building and Running

### Using VS Code Tasks

Press `Ctrl+Shift+P` and type "Tasks: Run Task" to see available tasks:

- **Gradle: Build** - Build the project
- **Gradle: Clean** - Clean build artifacts
- **Gradle: Assemble Debug** - Build debug APK
- **Gradle: Assemble Release** - Build release APK
- **Gradle: Install Debug** - Install debug APK on connected device
- **Gradle: Run Tests** - Run unit tests

### Using Terminal

```powershell
# Build
.\gradlew build

# Clean
.\gradlew clean

# Build debug APK
.\gradlew assembleDebug

# Install on device
.\gradlew installDebug

# Run tests
.\gradlew test
```

## Debugging

1. Set breakpoints in your Java/Kotlin code
2. Run your app on an emulator or connected device
3. Attach debugger using the "Debug Android App" configuration in launch.json
4. Or use the Java debugger extension

## Project Structure

A typical Android project structure:

```
app/
├── src/
│   ├── main/
│   │   ├── java/          # Java/Kotlin source files
│   │   ├── res/           # Resources (layouts, drawables, values)
│   │   └── AndroidManifest.xml
│   └── test/              # Unit tests
├── build.gradle           # App-level build config
└── build/
build.gradle               # Project-level build config
settings.gradle            # Project settings
```

## Troubleshooting

### Java Language Server Issues

- Reload VS Code window: `Ctrl+Shift+P` → "Developer: Reload Window"
- Clean Java workspace: `Ctrl+Shift+P` → "Java: Clean Java Language Server Workspace"

### Gradle Sync Issues

- Check `local.properties` file exists with `sdk.dir` path
- Verify Android SDK path in settings.json
- Run `.\gradlew --refresh-dependencies`

### Build Errors

- Ensure JDK is properly installed and JAVA_HOME is set
- Check Android SDK components are installed (SDK Platform, Build Tools)
- Verify Gradle wrapper version compatibility

## Additional Resources

- [Android Developer Documentation](https://developer.android.com/)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Gradle User Guide](https://docs.gradle.org/)
- [VS Code Java Extension Guide](https://code.visualstudio.com/docs/java/java-tutorial)

## Notes

- This configuration is optimized for Windows
- Adjust paths according to your system setup
- For Flutter development, install the Flutter extension separately
