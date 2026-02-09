# Getting Started with Reel Counter

Welcome! This guide will help you get started with the Reel Counter Android app development.

## Prerequisites

Before you begin, make sure you have:

1. **Android Studio** (Hedgehog 2023.1.1 or newer)
   - Download from: https://developer.android.com/studio
   
2. **Java Development Kit (JDK)** 8 or higher
   - Usually bundled with Android Studio
   
3. **Git** for version control
   - Download from: https://git-scm.com/

## Step 1: Clone the Repository

```bash
git clone https://github.com/sc-vigp/reel-counter-android-app.git
cd reel-counter-android-app
```

## Step 2: Open in Android Studio

1. Launch Android Studio
2. Click on "Open an Existing Project"
3. Navigate to the `reel-counter-android-app` folder
4. Click "OK"

## Step 3: First-Time Setup

When you first open the project:

1. **Gradle Sync**: Android Studio will automatically start syncing Gradle
   - This downloads all dependencies (may take a few minutes)
   - You'll see a progress bar at the bottom
   
2. **SDK Installation**: If prompted, install any missing SDK components
   - Android Studio will guide you through this
   
3. **Wait for indexing**: Let Android Studio index the project files

## Step 4: Understanding the Project Structure

```
reel-counter-android-app/
├── app/                          # Main application module
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/reelcounter/app/
│   │   │   │   ├── MainActivity.kt         # Entry point
│   │   │   │   ├── data/                   # Data layer (models, repository)
│   │   │   │   ├── viewmodel/              # ViewModels (business logic)
│   │   │   │   └── ui/                     # UI components (Composables)
│   │   │   ├── res/                        # Resources (strings, colors, etc.)
│   │   │   └── AndroidManifest.xml         # App configuration
│   │   └── test/                           # Unit tests
│   └── build.gradle.kts                    # App dependencies
├── gradle/                                  # Gradle wrapper files
├── build.gradle.kts                        # Project configuration
└── settings.gradle.kts                     # Project settings
```

## Step 5: Run the App

### Option A: Using Android Studio

1. **Set up a device**:
   - **Physical Device**: Enable USB debugging on your Android phone
   - **Emulator**: Create a virtual device (AVD) with API 24+
     - Tools → Device Manager → Create Device
     
2. **Run the app**:
   - Click the green "Run" button (▶) in the toolbar
   - Or press `Shift + F10`
   - Select your device from the dropdown
   
3. **View the app**: The app will launch on your device/emulator

### Option B: Using Command Line

```bash
# Build the app
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug
```

## Step 6: Understanding the Code

### MainActivity.kt
The entry point of the app. Sets up Jetpack Compose and initializes the ViewModel.

### ReelCounterScreen.kt
The main UI screen built with Jetpack Compose. Contains:
- Counter display
- "Add Reel" button
- "Reset Counter" button

### ReelCounterViewModel.kt
Manages the UI state using the MVVM pattern:
- Observes data from the repository
- Exposes state to the UI via StateFlow
- Handles user actions

### ReelRepository.kt
Data management layer:
- Currently uses in-memory storage
- Ready to integrate with Room database
- Provides data to ViewModel

## Step 7: Making Your First Change

Let's change the app name:

1. Open `app/src/main/res/values/strings.xml`
2. Find: `<string name="app_name">Reel Counter</string>`
3. Change to: `<string name="app_name">My Reel Tracker</string>`
4. Run the app to see the change

## Step 8: Next Steps

### Learn the Basics
- Explore the Kotlin code in each file
- Understand how data flows from Repository → ViewModel → UI
- Modify the UI colors in `values/colors.xml`

### Add Features
- **Persistent Storage**: Implement Room database
- **Multiple Platforms**: Add platform selection (Instagram, TikTok, etc.)
- **Statistics Screen**: Show charts and graphs
- **Settings**: Add user preferences

### Resources
- [Jetpack Compose Tutorial](https://developer.android.com/jetpack/compose/tutorial)
- [Android Basics with Compose](https://developer.android.com/courses/android-basics-compose/course)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)

## Common Issues

### Gradle Sync Failed
**Solution**: 
- Check your internet connection
- Click "File" → "Invalidate Caches / Restart"
- Delete `.gradle` folder and sync again

### App Crashes on Launch
**Solution**:
- Check LogCat for error messages
- Ensure emulator/device is API 24+
- Clean and rebuild: Build → Clean Project → Rebuild Project

### Cannot Find SDK
**Solution**:
- Open SDK Manager (Tools → SDK Manager)
- Install Android SDK 34
- Set ANDROID_HOME environment variable

## Tips for Beginners

1. **Use LogCat**: View logs and errors
   - View → Tool Windows → Logcat
   
2. **Code Completion**: Press `Ctrl+Space` for suggestions

3. **Quick Fixes**: When you see red errors, press `Alt+Enter` for quick fixes

4. **Documentation**: Hover over any code and press `Ctrl+Q` for documentation

5. **Run Tests**: Right-click on test file → Run

## Getting Help

- Check the [README.md](README.md) for project overview
- Read [CONTRIBUTING.md](CONTRIBUTING.md) for contribution guidelines
- Open an issue on GitHub for questions
- Explore Android documentation: https://developer.android.com

## What's Next?

Now that you have the app running:

1. ✅ Understand the current code
2. ✅ Make small UI changes
3. ✅ Add a new feature (start with something simple)
4. ✅ Write tests for your code
5. ✅ Commit and push your changes

Happy coding! 🚀 You're on your way to becoming an Android developer!
