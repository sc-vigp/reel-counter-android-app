# Reel Counter Android App

An Android app that tracks the reels you scroll through. Because one hour content creation is not equal to one hour of doom-scrolling.

## Overview

Reel Counter is a starter template for an Android application that helps users track their social media reel consumption throughout the day. This template follows Android development best practices and serves as a foundation for building a comprehensive usage tracking app.

## Features

- 📊 **Daily Reel Counter**: Keep track of how many reels you watch each day
- ➕ **Easy Tracking**: Simple button to increment your reel count
- 🔄 **Reset Function**: Reset your daily counter anytime
- 🎨 **Material Design 3**: Modern UI following Material Design guidelines
- 🏗️ **MVVM Architecture**: Clean architecture pattern for maintainability
- 🚀 **Jetpack Compose**: Modern declarative UI toolkit

## Architecture

This app follows the **MVVM (Model-View-ViewModel)** architecture pattern:

```
├── data/
│   ├── ReelEntry.kt          # Data models
│   └── ReelRepository.kt     # Data management layer
├── viewmodel/
│   └── ReelCounterViewModel.kt  # Business logic and UI state
└── ui/
    ├── ReelCounterScreen.kt  # Main screen composable
    └── theme/                # Theme configuration
```

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Concurrency**: Kotlin Coroutines & Flow
- **Dependency Injection**: Manual (can be extended with Hilt/Koin)
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

## Project Structure

```
app/
├── build.gradle.kts           # App-level dependencies
├── src/main/
│   ├── AndroidManifest.xml    # App configuration
│   ├── java/com/reelcounter/app/
│   │   ├── MainActivity.kt    # Main entry point
│   │   ├── data/              # Data layer
│   │   ├── viewmodel/         # ViewModel layer
│   │   └── ui/                # UI layer
│   └── res/                   # Resources (layouts, strings, etc.)
build.gradle.kts               # Project-level configuration
settings.gradle.kts            # Project settings
```

## Getting Started

### Prerequisites

- **Android Studio**: Hedgehog (2023.1.1) or newer
- **JDK**: 8 or higher
- **Gradle**: 8.2 (included via wrapper)

### Setup Instructions

1. **Clone the repository**:
   ```bash
   git clone https://github.com/sc-vigp/reel-counter-android-app.git
   cd reel-counter-android-app
   ```

2. **Open in Android Studio**:
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory and select it

3. **Sync Gradle**:
   - Android Studio should automatically prompt to sync Gradle
   - If not, click "File" → "Sync Project with Gradle Files"

4. **Run the app**:
   - Connect an Android device or start an emulator
   - Click the "Run" button (green play icon) or press `Shift + F10`

### Building from Command Line

```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Run tests
./gradlew test
```

## Key Dependencies

- **AndroidX Core**: Core Android components
- **Jetpack Compose**: Declarative UI framework
- **Material3**: Material Design 3 components
- **Lifecycle**: ViewModel and LiveData
- **Kotlin Coroutines**: Asynchronous programming
- **Room** (ready to integrate): Local database

## Future Enhancements

This is a starter template. Here are some features you can add:

- [ ] Persistent storage with Room database
- [ ] Multiple platform tracking (Instagram, TikTok, YouTube, etc.)
- [ ] Time-based analytics and charts
- [ ] Daily/weekly/monthly statistics
- [ ] Usage notifications and limits
- [ ] Dark mode support
- [ ] Export data functionality
- [ ] Widget for quick counting

## Best Practices Implemented

✅ **MVVM Architecture**: Separation of concerns  
✅ **Jetpack Compose**: Modern UI development  
✅ **Kotlin Coroutines**: Efficient async operations  
✅ **StateFlow**: Reactive state management  
✅ **Material Design 3**: Consistent, modern UI  
✅ **Type Safety**: Kotlin's null safety features  
✅ **Single Activity**: Modern Android navigation pattern  

## Permissions

The app currently requests the following permissions (for future features):
- `INTERNET`: For potential cloud sync features
- `QUERY_ALL_PACKAGES`: To detect social media apps
- `PACKAGE_USAGE_STATS`: To track app usage automatically

## Contributing

This is a learning project. Feel free to fork and experiment with different features!

## License

This project is open source and available under the MIT License.

## Learn More

- [Android Developer Guide](https://developer.android.com/guide)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Material Design 3](https://m3.material.io/)
