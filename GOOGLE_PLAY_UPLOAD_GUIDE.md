# Google Play Store Upload Guide

This guide will help you upload your School Escape game to the Google Play Store.

## Prerequisites

1. **Google Play Developer Account**
   - Sign up at https://play.google.com/console/signup
   - Pay the one-time $25 registration fee
   - Complete your developer profile

2. **Java JDK** (for creating keystore)
   - Ensure Java JDK is installed
   - Set JAVA_HOME environment variable if needed

## Step 1: Create Release Keystore

A keystore is required to sign your app for Google Play. **Keep this file safe - you'll need it for all future updates!**

1. Run the keystore creation script:
   ```powershell
   .\create-keystore.ps1
   ```

2. Follow the prompts:
   - Enter a secure password (remember it!)
   - Choose a key alias (default: schoolescape-key)
   - The script will create `schoolescape-release-key.jks` and `keystore.properties`

3. **IMPORTANT**: 
   - Backup your keystore file securely
   - Add `keystore.properties` and `*.jks` to `.gitignore`
   - Never lose your keystore - Google Play requires the same key for all updates!

## Step 2: Update Application ID (Optional)

The current application ID is `com.schoolescape.game`. If you want to change it:

1. Edit `app/build.gradle.kts`
2. Change `applicationId = "com.schoolescape.game"` to your desired package name
3. Update package names in all Java files (use Android Studio's refactor feature)

## Step 3: Build Release AAB

Android App Bundle (AAB) is the required format for Google Play:

```powershell
.\build-release-aab.ps1
```

This will create: `app\build\outputs\bundle\release\app-release.aab`

## Step 4: Prepare Store Listing Assets

Before uploading, prepare these assets:

### Required Assets:

1. **App Icon**
   - 512x512 pixels PNG (no transparency)
   - Location: `app/src/main/res/mipmap-*/ic_launcher_foreground.png`

2. **Feature Graphic**
   - 1024x500 pixels PNG or JPG
   - Used in Google Play Store listing

3. **Screenshots** (at least 2, up to 8)
   - Phone: 16:9 or 9:16 aspect ratio
   - Minimum: 320px, Maximum: 3840px
   - Recommended: 1080x1920 or 1920x1080

4. **App Description**
   - Short description: 80 characters max
   - Full description: 4000 characters max

5. **Privacy Policy URL** (Required for most apps)
   - Host a privacy policy page
   - Include it in your store listing

### Optional Assets:

- Promotional video (YouTube)
- Tablet screenshots
- TV screenshots (if applicable)

## Step 5: Google Play Console Setup

1. **Create New App**
   - Go to https://play.google.com/console
   - Click "Create app"
   - Fill in:
     - App name: "School Escape"
     - Default language: Your language
     - App or game: Game
     - Free or paid: Free (or Paid)
     - Declarations: Complete all required

2. **Set Up Store Listing**
   - Go to Store presence → Main store listing
   - Upload app icon
   - Upload feature graphic
   - Add screenshots
   - Write app description
   - Add privacy policy URL
   - Set app category: Games → Action or Puzzle
   - Set content rating (complete questionnaire)

3. **Content Rating**
   - Complete the content rating questionnaire
   - Answer questions about your app's content
   - Get rating certificate (required before publishing)

4. **App Content**
   - Privacy Policy: Add URL
   - Data Safety: Complete the form about data collection
   - Target Audience: Set age group

## Step 6: Upload AAB

1. **Create Release**
   - Go to Production (or Testing → Internal testing)
   - Click "Create new release"
   - Upload your AAB file: `app\build\outputs\bundle\release\app-release.aab`

2. **Release Notes**
   - Add release notes (what's new in this version)
   - Example: "Initial release of School Escape game"

3. **Review Release**
   - Check all details
   - Ensure version code is correct (starts at 1)

4. **Save and Review**
   - Click "Save" then "Review release"
   - Fix any errors or warnings
   - Submit for review

## Step 7: Testing (Recommended)

Before going to production:

1. **Internal Testing**
   - Upload AAB to Internal testing track
   - Add testers (email addresses)
   - Test the app thoroughly

2. **Closed Testing**
   - Create a closed testing track
   - Invite beta testers
   - Gather feedback

3. **Open Testing** (Optional)
   - Public beta testing
   - Get wider feedback before production

## Step 8: Production Release

1. Once testing is complete, promote to Production
2. Google will review your app (can take 1-7 days)
3. Once approved, your app will be live on Google Play!

## Version Updates

For future updates:

1. Increment `versionCode` in `app/build.gradle.kts` (must be higher than previous)
2. Update `versionName` (e.g., "1.1", "1.2")
3. Build new AAB: `.\build-release-aab.ps1`
4. Upload to Google Play Console
5. Use the SAME keystore file!

## Troubleshooting

### Build Errors
- Ensure `keystore.properties` exists and is correct
- Check that keystore file path is correct
- Verify Java/JDK is installed

### Upload Errors
- Ensure AAB file is built in release mode
- Check that version code is incremented
- Verify app signing is configured correctly

### Google Play Console Errors
- Complete all required sections (Store listing, Content rating, etc.)
- Ensure privacy policy URL is accessible
- Check that screenshots meet requirements

## Important Notes

- **Keystore Security**: Never share your keystore file or passwords
- **Version Codes**: Must always increase (1, 2, 3, ...)
- **Testing**: Always test thoroughly before production release
- **Compliance**: Ensure your app complies with Google Play policies
- **Updates**: Keep your app updated to maintain compatibility

## Resources

- [Google Play Console](https://play.google.com/console)
- [Android App Bundle Guide](https://developer.android.com/guide/app-bundle)
- [Google Play Policies](https://play.google.com/about/developer-content-policy/)
- [App Signing Best Practices](https://developer.android.com/studio/publish/app-signing)

Good luck with your release! 🎮🚀

