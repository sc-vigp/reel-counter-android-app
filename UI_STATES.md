# UI State Diagram

This document shows the different UI states of the Reel Counter app with the auto-detection feature.

## State 1: Permission Not Granted

```
┌─────────────────────────────────────┐
│         Reel Counter                │
├─────────────────────────────────────┤
│                                     │
│   Track Your Reel Usage             │
│                                     │
│  ┌─────────────────────────────┐   │
│  │   Total Reels Today         │   │
│  │                             │   │
│  │         42                  │   │
│  │        reels                │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │  ○ Monitoring Stopped       │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │  ⚠️ Permission Required      │   │
│  │                             │   │
│  │  Usage Access permission    │   │
│  │  is required for            │   │
│  │  auto-detection to work     │   │
│  │                             │   │
│  │  [ Grant Permission ]       │   │
│  └─────────────────────────────┘   │
│                                     │
│  [ + Add Reel Manually ]           │
│                                     │
│  [ Reset Counter ]                 │
│                                     │
│  Start monitoring to automatically  │
│  count reels when you open social   │
│  media apps                         │
└─────────────────────────────────────┘
```

## State 2: Monitoring Stopped (Permission Granted)

```
┌─────────────────────────────────────┐
│         Reel Counter                │
├─────────────────────────────────────┤
│                                     │
│   Track Your Reel Usage             │
│                                     │
│  ┌─────────────────────────────┐   │
│  │   Total Reels Today         │   │
│  │                             │   │
│  │         42                  │   │
│  │        reels                │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │  ○ Monitoring Stopped       │   │
│  └─────────────────────────────┘   │
│                                     │
│  [ ▶ Start Monitoring ]            │
│                                     │
│  [ + Add Reel Manually ]           │
│                                     │
│  [ Reset Counter ]                 │
│                                     │
│  Start monitoring to automatically  │
│  count reels when you open social   │
│  media apps                         │
└─────────────────────────────────────┘
```

## State 3: Monitoring Active

```
┌─────────────────────────────────────┐
│         Reel Counter                │
├─────────────────────────────────────┤
│                                     │
│   Track Your Reel Usage             │
│                                     │
│  ┌─────────────────────────────┐   │
│  │   Total Reels Today         │   │
│  │                             │   │
│  │         42                  │   │
│  │        reels                │   │
│  └─────────────────────────────┘   │
│                                     │
│  ┌─────────────────────────────┐   │
│  │  ● Monitoring Active        │   │  (Green)
│  └─────────────────────────────┘   │
│                                     │
│  [ ⏸ Stop Monitoring ]             │  (Red)
│                                     │
│  [ ✓ Auto Mode Active ]            │  (Disabled)
│                                     │
│  [ Reset Counter ]                 │
│                                     │
│  App is automatically tracking      │
│  Instagram, Facebook, and WhatsApp  │
│  opens                              │
└─────────────────────────────────────┘
```

## Notification (When Monitoring)

```
┌─────────────────────────────────────┐
│  Reel Counter Active                │
│  Monitoring app usage...            │
│                                     │
│  [Tap to open app]                 │
└─────────────────────────────────────┘
```

## Key UI Elements

### Buttons
1. **Grant Permission** (Red button in warning card)
   - Visible: When permission not granted
   - Action: Opens system settings for Usage Access

2. **▶ Start Monitoring** (Blue primary button)
   - Visible: When monitoring is stopped and permission granted
   - Action: Starts the background service

3. **⏸ Stop Monitoring** (Red button)
   - Visible: When monitoring is active
   - Action: Stops the background service

4. **+ Add Reel Manually** (Blue primary button when enabled)
   - Enabled: When monitoring is stopped
   - Disabled: When monitoring is active (shows "✓ Auto Mode Active")
   - Action: Manually increment counter

5. **Reset Counter** (Outlined button)
   - Always visible and enabled
   - Action: Resets today's count to 0

### Status Indicators
1. **○ Monitoring Stopped** (Gray card)
   - Shows when service is not running

2. **● Monitoring Active** (Green card)
   - Shows when service is actively monitoring

### Warning Card
- **⚠️ Permission Required** (Red card)
- Shows when PACKAGE_USAGE_STATS permission is not granted
- Includes explanation and button to grant permission

## User Journey

### First Time User
1. Opens app → Sees permission warning
2. Clicks "Grant Permission" → Goes to Settings
3. Enables "Usage Access" → Returns to app
4. Clicks "▶ Start Monitoring" → Service starts
5. Opens Instagram → Counter increments automatically
6. Returns to app → Sees updated count

### Returning User (Monitoring Stopped)
1. Opens app → No permission warning
2. Sees current count
3. Clicks "▶ Start Monitoring" → Service starts
4. App continues to monitor in background

### Monitoring Active
1. User opens Instagram/Facebook/WhatsApp
2. Background service detects app open
3. Counter increments automatically
4. Notification shows detection
5. User can return to app to see updated count

## Color Scheme

- **Primary**: Blue (Start button, counter number)
- **Success/Active**: Green (Monitoring Active indicator)
- **Error/Stop**: Red (Stop button, permission warning)
- **Neutral**: Gray (Stopped indicator, disabled button)
- **Background**: White/Surface colors
