# Auto-Detection Feature

## Overview
The Reel Counter app now includes an auto-detection feature that automatically tracks when you open Instagram, Facebook, or WhatsApp and increments the counter for each app open.

## How It Works

### 1. Background Service
- The app uses a **Foreground Service** (`AppUsageMonitorService`) that runs in the background
- The service displays a persistent notification to inform the user it's actively monitoring
- The service checks every 2 seconds for app opens

### 2. App Usage Detection
- Uses Android's **UsageStatsManager** API to detect when apps are opened
- Monitors for the following apps:
  - Instagram (`com.instagram.android`)
  - Facebook (`com.facebook.katana`)
  - WhatsApp (`com.whatsapp`)
- When any of these apps move to the foreground, the counter is automatically incremented

### 3. Required Permissions
- **PACKAGE_USAGE_STATS**: Required to access app usage information
- **FOREGROUND_SERVICE**: Required to run the background service
- **POST_NOTIFICATIONS**: Required to display the monitoring notification

## User Flow

1. **First Time Setup**:
   - User opens the Reel Counter app
   - If Usage Access permission is not granted, a warning card is displayed
   - User clicks "Grant Permission" which takes them to system settings
   - User enables "Usage Access" for Reel Counter app

2. **Starting Monitoring**:
   - User clicks "▶ Start Monitoring" button
   - The foreground service starts
   - A notification appears showing "Reel Counter Active"
   - Manual "Add Reel" button is disabled (auto mode is active)

3. **During Monitoring**:
   - Every time user opens Instagram, Facebook, or WhatsApp
   - The counter automatically increments
   - The notification updates showing the detection
   - User can see the count in real-time in the app

4. **Stopping Monitoring**:
   - User clicks "⏸ Stop Monitoring" button
   - The service stops
   - Manual "Add Reel Manually" button becomes enabled again

## UI Changes

### Status Indicators
- **Monitoring Active**: Green card showing "● Monitoring Active"
- **Monitoring Stopped**: Gray card showing "○ Monitoring Stopped"

### Permission Warning
- Red warning card appears if Usage Access permission is not granted
- Contains "Grant Permission" button to navigate to settings

### Buttons
- **Start/Stop Monitoring**: Primary button that toggles monitoring state
- **Add Reel Manually**: Disabled when monitoring is active, enabled when stopped
- **Reset Counter**: Always available to reset daily count

### Info Text
- When monitoring: "App is automatically tracking Instagram, Facebook, and WhatsApp opens"
- When stopped: "Start monitoring to automatically count reels when you open social media apps"

## Implementation Details

### Key Classes

1. **AppUsageDetector.kt**
   - Handles querying UsageStatsManager
   - Detects app open events (MOVE_TO_FOREGROUND)
   - Checks permission status
   - Maintains list of monitored apps

2. **AppUsageMonitorService.kt**
   - Foreground service that runs in background
   - Creates and maintains notification
   - Periodically checks for app opens (every 2 seconds)
   - Automatically increments counter via Repository

3. **ReelCounterViewModel.kt**
   - Manages service state (isMonitoring)
   - Provides startMonitoring() and stopMonitoring() methods
   - Communicates between UI and Service

4. **MainActivity.kt**
   - Checks permission status on launch
   - Provides method to request permissions
   - Passes state to UI composables

5. **ReelCounterScreen.kt**
   - Displays monitoring status
   - Shows permission warning if needed
   - Manages button states based on monitoring status

## Testing the Feature

### Manual Testing Steps

1. **Permission Flow**:
   - Install the app
   - Open it without granting permissions
   - Verify warning card appears
   - Click "Grant Permission"
   - Enable Usage Access in settings
   - Return to app and verify warning disappears

2. **Monitoring Flow**:
   - Click "Start Monitoring"
   - Verify notification appears
   - Open Instagram (or Facebook/WhatsApp)
   - Return to Reel Counter app
   - Verify counter has incremented

3. **Manual Mode**:
   - Click "Stop Monitoring"
   - Verify "Add Reel Manually" button is enabled
   - Click it to increment counter manually

4. **Reset**:
   - Click "Reset Counter"
   - Verify count goes back to 0

### Known Limitations

1. **Permission Required**: Usage Access is a sensitive permission that must be manually granted in system settings
2. **Battery Usage**: Background monitoring consumes battery (minimal with 2-second intervals)
3. **Accuracy**: Detects app opens, not specifically reel viewing
4. **Notification**: Persistent notification is required for foreground service (Android requirement)

## Future Enhancements

1. **Configurable Apps**: Allow users to select which apps to monitor
2. **Adjustable Interval**: Let users configure check interval (balance accuracy vs battery)
3. **Detailed Stats**: Show per-app statistics
4. **Time Tracking**: Track duration in each app, not just opens
5. **Smart Detection**: Use accessibility service to detect actual reel viewing
6. **Background Sync**: Persist service state across app restarts
