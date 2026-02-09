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
- **Build System**: Gradle with Kotlin DSL
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
│   │   │   ├── ReelEntry.kt   # Data models
│   │   │   └── ReelRepository.kt  # Repository pattern
│   │   ├── viewmodel/         # ViewModel layer
│   │   │   └── ReelCounterViewModel.kt
│   │   └── ui/                # UI layer
│   │       ├── ReelCounterScreen.kt  # Main screen
│   │       └── theme/         # App theming
│   └── res/                   # Resources (layouts, strings, etc.)
│       ├── values/
│       │   ├── strings.xml
│       │   ├── colors.xml
│       │   └── themes.xml
│       ├── drawable/          # Icons and graphics
│       └── mipmap-*/          # Launcher icons
build.gradle.kts               # Project-level configuration
settings.gradle.kts            # Project settings
```

## Getting Started

### Prerequisites

- **Android Studio**: Hedgehog (2023.1.1) or newer
- **JDK**: 8 or higher
- **Gradle**: 8.2 (included via wrapper)
- **Android SDK**: API 24 or higher

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
   - The IDE will download all necessary dependencies

4. **Run the app**:
   - Connect an Android device or start an emulator (API 24+)
   - Click the "Run" button (green play icon) or press `Shift + F10`

### Building from Command Line

```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Run tests
./gradlew test

# Build release APK (unsigned)
./gradlew assembleRelease
```

## Code Structure Explained

### Data Layer (`data/`)

**ReelEntry.kt**: Defines the data model for a reel view
```kotlin
data class ReelEntry(
    val id: Long,
    val platform: String,
    val timestamp: Long,
    val date: String
)
```

**ReelRepository.kt**: Manages data operations (currently in-memory, ready for Room integration)
- `addReelEntry()`: Adds a new reel entry
- `getTodayCount()`: Returns today's reel count
- `resetTodayCounter()`: Resets the daily counter

### ViewModel Layer (`viewmodel/`)

**ReelCounterViewModel.kt**: Manages UI state and business logic
- Observes data from repository
- Exposes StateFlow for UI to observe
- Handles user actions (add reel, reset counter)

### UI Layer (`ui/`)

**ReelCounterScreen.kt**: Main composable UI
- Material Design 3 components
- Reactive UI with state management
- Clean, accessible design

**theme/**: App theming configuration
- Color.kt: Color definitions
- Type.kt: Typography styles
- Theme.kt: Main theme setup

## Key Dependencies

- **AndroidX Core**: Core Android components
- **Jetpack Compose**: Declarative UI framework (Material3)
- **Lifecycle**: ViewModel and LiveData
- **Kotlin Coroutines**: Asynchronous programming
- **Room** (ready to integrate): Local database

## Best Practices Implemented

✅ **MVVM Architecture**: Separation of concerns  
✅ **Jetpack Compose**: Modern UI development  
✅ **Kotlin Coroutines**: Efficient async operations  
✅ **StateFlow**: Reactive state management  
✅ **Material Design 3**: Consistent, modern UI  
✅ **Type Safety**: Kotlin's null safety features  
✅ **Single Activity**: Modern Android navigation pattern  
✅ **Repository Pattern**: Abstraction over data sources  
✅ **Unidirectional Data Flow**: Clear data flow pattern  

## Future Enhancements

This is a starter template. Here are features you can add iteratively:

### Phase 1: Core Functionality
- [ ] Persistent storage with Room database
- [ ] Time-based tracking (duration)
- [ ] Daily statistics view

### Phase 2: Multi-Platform
- [ ] Track multiple platforms (Instagram, TikTok, YouTube, etc.)
- [ ] Platform selection UI
- [ ] Platform-specific statistics

### Phase 3: Analytics
- [ ] Weekly/monthly charts and graphs
- [ ] Usage patterns and insights
- [ ] Goal setting and tracking

### Phase 4: Advanced Features
- [ ] Usage notifications and limits
- [ ] Dark mode support
- [ ] Export data (CSV, JSON)
- [ ] Home screen widget
- [ ] Background tracking with AccessibilityService

## Permissions

The app requests the following permissions (configured for future features):

- `INTERNET`: For potential cloud sync features
- `QUERY_ALL_PACKAGES`: To detect social media apps
- `PACKAGE_USAGE_STATS`: To automatically track app usage

**Note**: Current implementation requires manual tracking. Future versions can use these permissions for automatic tracking.

## Development Notes

### Adding Room Database

To add persistent storage:

1. Uncomment Room annotation processor in `app/build.gradle.kts`
2. Create Entity classes with `@Entity` annotation
3. Create DAO interfaces with `@Dao` annotation
4. Create Database class with `@Database` annotation
5. Update Repository to use Database

### Testing

The project is set up for testing:
- Unit tests: `app/src/test/`
- Instrumentation tests: `app/src/androidTest/`

Run tests with:
```bash
./gradlew test  # Unit tests
./gradlew connectedAndroidTest  # Instrumentation tests
```

## Troubleshooting

**Issue**: Gradle sync fails  
**Solution**: Ensure you have a stable internet connection for first-time setup to download dependencies

**Issue**: App doesn't run on emulator  
**Solution**: Ensure emulator is running API level 24 or higher

**Issue**: Build errors  
**Solution**: Clean and rebuild: `Build > Clean Project` then `Build > Rebuild Project`

## Contributing

This is a learning project. Feel free to fork and experiment with different features!

## Resources

- [Android Developer Guide](https://developer.android.com/guide)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Material Design 3](https://m3.material.io/)
- [Android Architecture Guide](https://developer.android.com/topic/architecture)

## License

This project is open source and available under the MIT License.

---

**Happy coding! 🚀** Start tracking those reels and take control of your social media usage!
