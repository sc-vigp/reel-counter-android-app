# Phase 2 & 3 Implementation Plan

## Overview
Implement user onboarding flow (Phase 2) and automatic YouTube Shorts detection (Phase 3) for the accessibility-based tracking system.

---

## Phase 2: User Onboarding (Permission Flow)

### Objective
Guide users through enabling the Accessibility Service in a smooth, frictionless way.

### Tasks

#### 2.1: Create Accessibility Service Status Checker Utility
**File**: `app/src/main/java/com/reelcounter/app/utils/AccessibilityServiceUtils.kt`

- [ ] Function: `isAccessibilityServiceEnabled(context: Context, serviceClass: Class<*>): Boolean`
  - Check if our Accessibility Service is enabled in system settings
  - Uses AccessibilityManager.getEnabledAccessibilityServiceList()
  
- [ ] Function: `openAccessibilitySettings(context: Context)`
  - Opens Android Accessibility Settings activity
  - Deep-links directly to the service being requested

**Deliverable**: Utility functions for checking and enabling the service

---

#### 2.2: Update ReelCounterViewModel to Track Service Status
**File**: `app/src/main/java/com/reelcounter/app/viewmodel/ReelCounterViewModel.kt`

- [ ] Add StateFlow: `isAccessibilityServiceEnabled: StateFlow<Boolean>`
- [ ] Function: `checkAccessibilityServiceStatus()`
  - Calls AccessibilityServiceUtils.isAccessibilityServiceEnabled()
  - Updates StateFlow with current status
  
- [ ] Function: `openAccessibilitySettings()`
  - Calls AccessibilityServiceUtils.openAccessibilitySettings()
  
- [ ] Add lifecycle awareness
  - Check service status on app resume (to detect if user manually enabled it)

**Deliverable**: ViewModel exposes service status and settings navigation

---

#### 2.3: Create Accessibility Onboarding UI Component
**File**: `app/src/main/java/com/reelcounter/app/ui/AccessibilityPermissionCard.kt`

- [ ] Composable: `AccessibilityPermissionCard()`
  - Shows when service is disabled
  - Displays explanation of what the service does
  - Has "Enable Accessibility" button
  - Shows status icon/text when enabled
  
- [ ] Composable: `AccessibilityStatusBadge()`
  - Small badge showing current status
  - Green checkmark when enabled
  - Warning icon when disabled

**Deliverable**: Reusable UI components for service status display

---

#### 2.4: Integrate Onboarding into ReelCounterScreen
**File**: `app/src/main/java/com/reelcounter/app/ui/ReelCounterScreen.kt`

- [ ] Add AccessibilityPermissionCard at top of screen
- [ ] Show conditional UI based on `isAccessibilityServiceEnabled` state
- [ ] Add LaunchedEffect to check service status on screen composition
- [ ] Handle navigation to accessibility settings when button clicked

**Deliverable**: Main screen includes onboarding flow

---

#### 2.5: Update MainActivity Lifecycle
**File**: `app/src/main/java/com/reelcounter/app/MainActivity.kt`

- [ ] In onResume(): Call `viewModel.checkAccessibilityServiceStatus()`
  - Ensures we update UI if user manually enabled service in Settings
  
- [ ] Handle back navigation from Settings
  - Auto-refresh status when returning to app

**Deliverable**: App detects service enablement changes

---

### Phase 2 End Product
**Expected Behavior**:
1. User opens app
2. Sees "Enable Auto-Tracking" card at top (if service not enabled)
3. Clicks button → Opens Android Accessibility Settings automatically
4. User toggles on our service
5. Returns to app → Card automatically updates to show "Active ✓"
6. Ready for Phase 3 detection to begin

---

## Phase 3: Detection Heuristics & Auto-Counting

### Objective
Detect YouTube Shorts scrolls and automatically increment counter without user input.

### Tasks

#### 3.1: Implement Scroll Event Debouncing
**File**: `app/src/main/java/com/reelcounter/app/service/YouTubeReelAccessibilityService.kt`

- [ ] Add debounce timer property: `lastScrollEventTime: Long = 0`
- [ ] Add debounce threshold constant: `SCROLL_DEBOUNCE_MS = 500L`
  
- [ ] Function: `isScrollEventValid(currentTime: Long): Boolean`
  - Returns true only if time since last event > SCROLL_DEBOUNCE_MS
  - Prevents counting one physical swipe as 5 events
  
- [ ] Update `lastScrollEventTime` after each counted scroll

**Deliverable**: Debouncer prevents duplicate counting of single swipes

---

#### 3.2: Implement YouTube Package Filtering
**File**: `app/src/main/java/com/reelcounter/app/service/YouTubeReelAccessibilityService.kt`

- [ ] Update `onAccessibilityEvent()`:
  - Check `event.packageName == YOUTUBE_PACKAGE`
  - Ignore all non-YouTube events
  - Log only YouTube events

- [ ] Add event type filtering:
  - Focus on `TYPE_VIEW_SCROLLED` (main scroll detection)
  - Also monitor `TYPE_WINDOW_STATE_CHANGED` for Shorts transitions

**Deliverable**: Service only processes YouTube events

---

#### 3.3: Implement Basic Scroll Detection (Phase 3a)
**File**: `app/src/main/java/com/reelcounter/app/service/YouTubeReelAccessibilityService.kt`

- [ ] Function: `processScrollEvent(event: AccessibilityEvent)`
  - Filter for TYPE_VIEW_SCROLLED
  - Apply debouncing check
  - Validate event source (not null)
  
- [ ] Call repository to add reel:
  - `viewModelScope.launch { repository.addReelEntry(platform = "YouTube Shorts") }`
  - Use coroutines to avoid blocking accessibility thread

- [ ] Add logging:
  - Log each counted scroll
  - Tag as "YouTubeScrollDetected"

**Deliverable**: App auto-increments counter on YouTube scrolls (including false positives)

---

#### 3.4: Implement Shorts-Only Detection (Phase 3b - Refinement)
**File**: `app/src/main/java/com/reelcounter/app/service/YouTubeReelAccessibilityService.kt`

- [ ] Function: `isInShortsSection(event: AccessibilityEvent): Boolean`
  - Analyze window content to determine if in Shorts feed
  - Look for Shorts-specific UI elements:
    - Shorts player view
    - Vertical scrolling container (indicates Shorts)
    - Absence of horizontal feed navigation
  
- [ ] Collect view hierarchy information:
  - `event.source?.let { root -> findShortsIndicators(root) }`
  
- [ ] Store Shorts detection state:
  - `currentViewMode: ViewMode = ViewMode.UNKNOWN`
  - Enum: `SHORTS`, `NORMAL_FEED`, `UNKNOWN`

- [ ] Only count scrolls when `ViewMode == SHORTS`

**Deliverable**: App accurately differentiates Shorts vs normal feed scrolls

---

#### 3.5: Add Scroll Detection Logging & Monitoring
**File**: `app/src/main/java/com/reelcounter/app/service/YouTubeReelAccessibilityService.kt`

- [ ] Add event statistics tracking:
  - Total events processed
  - Shorts detected
  - Scrolls counted
  - Detection accuracy metrics
  
- [ ] Create debug logging function:
  - Log event details: timestamp, type, source
  - Log detection decisions: was it counted? why/why not?
  
- [ ] Make toggleable via SharedPreferences:
  - Debug mode flag
  - Can be toggled from app settings

**Deliverable**: Detailed logging for debugging and optimization

---

#### 3.6: Update ReelRepository for Coroutine Safety
**File**: `app/src/main/java/com/reelcounter/app/data/ReelRepository.kt`

- [ ] Ensure addReelEntry() is suspend-safe
- [ ] Add timestamp to ReelEntry:
  - `timestamp: Long = System.currentTimeMillis()`
  - Helps track detection patterns
  
- [ ] Add platform field to filter by source:
  - `val platform: String = "YouTube Shorts"`

- [ ] Handle rapid consecutive calls gracefully

**Deliverable**: Repository correctly stores auto-detected shorts

---

#### 3.7: Add Accessibility Service Permissions Check
**File**: `app/src/main/java/com/reelcounter/app/service/YouTubeReelAccessibilityService.kt`

- [ ] In `onServiceConnected()`:
  - Start a timer to verify service stays connected
  - Log if service connection is interrupted
  
- [ ] Error handling:
  - Gracefully handle accessibility exceptions
  - Log errors without crashing service

**Deliverable**: Robust error handling in accessibility service

---

### Phase 3 End Product
**Expected Behavior**:
1. User enables service (Phase 2)
2. Opens YouTube app
3. Opens YouTube Shorts
4. Swipes through shorts
5. Each swipe automatically increments counter
6. Minimal false positives (Phase 3b refinement)
7. App maintains accurate count throughout session
8. Counter syncs between app UI and accessibility service

---

## Testing Checklist

### Phase 2 Testing
- [ ] Service status check works correctly
- [ ] Button correctly opens Settings app
- [ ] Service enable/disable is detected
- [ ] UI updates when service status changes
- [ ] Works after app restart

### Phase 3 Testing
- [ ] Counter increments on YouTube scrolls
- [ ] Debouncing prevents duplicate counts
- [ ] No counts when not in YouTube
- [ ] Shorts-specific detection works (Phase 3b)
- [ ] Handles rapid scrolling gracefully
- [ ] No memory leaks in service
- [ ] Service survives configuration changes

---

## Dependencies & Notes

### New Dependencies Needed
- None (uses Android platform APIs)

### Architectural Decisions
1. **Coroutines in Accessibility Service**: Use coroutines for non-blocking operations
2. **Debouncing Strategy**: Time-based (500ms) vs event-based - chose time-based for simplicity
3. **Shorts Detection**: UI hierarchy analysis vs heuristics - will use both
4. **Logging**: Debug mode toggleable to reduce performance impact

### Known Challenges
1. **YouTube UI Changes**: Shorts detection may break if YouTube redesigns
2. **False Positives**: Hard to distinguish Shorts scroll from normal feed scroll
3. **Accessibility Fragility**: Service can be killed by OS under memory pressure
4. **Performance**: Analyzing view hierarchy on every event may impact performance

### Mitigation Strategies
1. Monitor YouTube app version and adjust detection as needed
2. Implement pattern learning to improve accuracy over time
3. Add foreground service notification to keep service alive
4. Cache view hierarchy analysis results

---

## Files to Create/Modify

### New Files
- `AccessibilityServiceUtils.kt` - Utility functions
- `AccessibilityPermissionCard.kt` - UI components

### Modified Files
- `ReelCounterViewModel.kt` - Add service status tracking
- `ReelCounterScreen.kt` - Integrate onboarding UI
- `MainActivity.kt` - Add lifecycle checks
- `YouTubeReelAccessibilityService.kt` - Add detection logic
- `ReelRepository.kt` - Ensure coroutine safety

### Files Unchanged
- `AndroidManifest.xml` (already configured)
- `accessibility_service_config.xml` (already configured)

---

## Commit Strategy

Each meaningful sub-task will be a separate commit:

**Phase 2 Commits**:
1. `feat: Add AccessibilityServiceUtils for service status checking`
2. `feat: Add accessibility service status to ViewModel`
3. `feat: Create AccessibilityPermissionCard UI component`
4. `feat: Integrate accessibility onboarding into ReelCounterScreen`
5. `feat: Add lifecycle handling in MainActivity`

**Phase 3 Commits**:
6. `feat: Implement scroll event debouncing in accessibility service`
7. `feat: Add YouTube package filtering for events`
8. `feat: Implement basic scroll detection logic`
9. `feat: Add Shorts-specific detection heuristics`
10. `feat: Add scroll detection logging and monitoring`
11. `refactor: Update ReelRepository for coroutine safety`
12. `feat: Add accessibility service error handling`

---

## Timeline Estimate

- **Phase 2**: 4-5 hours
  - Utilities: 30 min
  - ViewModel updates: 45 min
  - UI components: 1.5 hours
  - Screen integration: 1 hour
  - Testing: 1 hour

- **Phase 3**: 6-8 hours
  - Debouncing: 30 min
  - Event filtering: 45 min
  - Basic detection: 1.5 hours
  - Shorts detection: 2 hours
  - Logging: 1 hour
  - Error handling: 1 hour
  - Testing: 1.5 hours

**Total**: 10-13 hours of development

---

## Success Criteria

✅ Phase 2 Complete When:
- User sees onboarding card in app
- Button opens Settings correctly
- Service status auto-updates
- No errors on enable/disable cycles

✅ Phase 3 Complete When:
- Opening YouTube Shorts increments counter
- No counts when not in YouTube
- Debouncing prevents duplicate counts
- Shorts-specific detection minimizes false positives
- All logs are clean (no crashes)
- Service remains stable throughout session
