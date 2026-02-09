# Quick Start Cheat Sheet

## 🚀 In 5 Minutes

```bash
# 1. Clone
git clone https://github.com/sc-vigp/reel-counter-android-app.git
cd reel-counter-android-app

# 2. Open in Android Studio
# File → Open → Select this folder

# 3. Wait for Gradle sync to complete

# 4. Run
# Click the green ▶ button or press Shift+F10
```

## 📁 What's Where

| File/Folder | Purpose |
|------------|---------|
| `app/src/main/java/...` | All Kotlin source code |
| `app/src/main/res/` | UI resources (strings, colors, icons) |
| `app/build.gradle.kts` | Dependencies |
| `README.md` | Full documentation |
| `GETTING_STARTED.md` | Beginner's guide |

## 🎯 Key Files to Understand

1. **MainActivity.kt** - App entry point
2. **ReelCounterScreen.kt** - UI layout
3. **ReelCounterViewModel.kt** - Business logic
4. **ReelRepository.kt** - Data management

## 💡 Common Tasks

### Change App Name
```xml
<!-- app/src/main/res/values/strings.xml -->
<string name="app_name">Your App Name</string>
```

### Change Theme Color
```xml
<!-- app/src/main/res/values/colors.xml -->
<color name="primary">#YOUR_COLOR</color>
```

### Add a New Feature
1. Update data model in `ReelEntry.kt`
2. Add function in `ReelRepository.kt`
3. Add function in `ReelCounterViewModel.kt`
4. Update UI in `ReelCounterScreen.kt`

## 🛠️ Build Commands

```bash
# Debug build
./gradlew assembleDebug

# Install on device
./gradlew installDebug

# Run tests
./gradlew test

# Clean build
./gradlew clean build
```

## 📱 Testing

```bash
# Unit tests
./gradlew test

# View test report
open app/build/reports/tests/testDebugUnitTest/index.html
```

## 🐛 Troubleshooting

| Problem | Solution |
|---------|----------|
| Gradle sync fails | Invalidate Caches: File → Invalidate Caches / Restart |
| App won't run | Check emulator is API 24+ |
| Build errors | Build → Clean Project, then Rebuild |
| Can't find SDK | Tools → SDK Manager → Install SDK 34 |

## 📚 Learning Path

1. ✅ Run the app
2. ✅ Read `MainActivity.kt`
3. ✅ Understand `ReelCounterScreen.kt`
4. ✅ Learn `ReelCounterViewModel.kt`
5. ✅ Explore `ReelRepository.kt`
6. ✅ Read `ARCHITECTURE.md`
7. ✅ Make your first change

## 🎓 Resources

- Full Guide: [GETTING_STARTED.md](GETTING_STARTED.md)
- Architecture: [ARCHITECTURE.md](ARCHITECTURE.md)
- Flow Diagrams: [FLOW_DIAGRAM.md](FLOW_DIAGRAM.md)
- Android Docs: https://developer.android.com

## ⌨️ Android Studio Shortcuts

| Action | Shortcut |
|--------|----------|
| Run app | `Shift + F10` |
| Build project | `Ctrl + F9` |
| Find file | `Ctrl + Shift + N` |
| Find in files | `Ctrl + Shift + F` |
| Reformat code | `Ctrl + Alt + L` |
| Quick fix | `Alt + Enter` |

## 🚀 Next Steps

### Beginner
- Change the app colors
- Modify button text
- Add new string resources

### Intermediate  
- Add Room database
- Create settings screen
- Add platform selector

### Advanced
- Implement charts
- Add widget
- Background tracking

---

**Need more help?** Check the full docs in the repo! 📖
