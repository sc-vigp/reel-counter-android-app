# App Flow Diagram

## User Interaction Flow

```
┌─────────────────────────────────────────────────────────────┐
│                    User Opens App                            │
└─────────────────────────┬───────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                   MainActivity Starts                        │
│  - Initializes ReelCounterViewModel                         │
│  - Sets up Jetpack Compose                                  │
└─────────────────────────┬───────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│              ReelCounterViewModel Initializes                │
│  - Gets ReelRepository instance                             │
│  - Observes repository data                                 │
│  - Initializes StateFlow with count = 0                     │
└─────────────────────────┬───────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│               ReelCounterScreen Displays                     │
│  ┌───────────────────────────────────────────────┐         │
│  │         Reel Counter (App Bar)                 │         │
│  ├───────────────────────────────────────────────┤         │
│  │                                                 │         │
│  │        Track Your Reel Usage                   │         │
│  │                                                 │         │
│  │  ┌─────────────────────────────────────┐      │         │
│  │  │   Total Reels Today                  │      │         │
│  │  │                                       │      │         │
│  │  │            0                         │      │         │
│  │  │          reels                       │      │         │
│  │  └─────────────────────────────────────┘      │         │
│  │                                                 │         │
│  │  ┌─────────────────────────────────────┐      │         │
│  │  │        + Add Reel                    │      │         │
│  │  └─────────────────────────────────────┘      │         │
│  │                                                 │         │
│  │  ┌─────────────────────────────────────┐      │         │
│  │  │       Reset Counter                  │      │         │
│  │  └─────────────────────────────────────┘      │         │
│  │                                                 │         │
│  │  Tap 'Add Reel' each time you watch a reel    │         │
│  │           on social media                      │         │
│  └────────────────────────────────────────────────┘         │
└─────────────────────────────────────────────────────────────┘
```

## When User Taps "Add Reel"

```
┌─────────────────────────┐
│   User Taps Button      │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────────────────┐
│  ReelCounterScreen.onAddReel()      │
│  - Button click callback triggered  │
└────────┬────────────────────────────┘
         │
         ▼
┌──────────────────────────────────────────┐
│  ReelCounterViewModel.addReel()          │
│  - Launched in viewModelScope            │
│  - Calls repository                      │
└────────┬─────────────────────────────────┘
         │
         ▼
┌──────────────────────────────────────────┐
│  ReelRepository.addReelEntry()           │
│  - Creates new ReelEntry                 │
│  - Adds to internal list                 │
│  - Updates StateFlow                     │
└────────┬─────────────────────────────────┘
         │
         ▼
┌──────────────────────────────────────────┐
│  Repository StateFlow Emits Change       │
│  - New list with added entry             │
└────────┬─────────────────────────────────┘
         │
         ▼
┌──────────────────────────────────────────┐
│  ViewModel Observes Change               │
│  - Calculates new count                  │
│  - Updates todayCount StateFlow          │
└────────┬─────────────────────────────────┘
         │
         ▼
┌──────────────────────────────────────────┐
│  UI Collects State Change                │
│  - todayCount.collectAsState()           │
│  - Compose recomposes automatically      │
└────────┬─────────────────────────────────┘
         │
         ▼
┌──────────────────────────────────────────┐
│  Screen Updates                          │
│  - Counter shows: 1, 2, 3...            │
│  - UI reflects new state                 │
└──────────────────────────────────────────┘
```

## Data Flow Architecture

```
┌──────────────────────────────────────────────────────────┐
│                     UI LAYER (Compose)                    │
│  ┌────────────────────────────────────────────────┐     │
│  │         ReelCounterScreen.kt                    │     │
│  │  - Displays count                               │     │
│  │  - Shows buttons                                │     │
│  │  - Handles user input                           │     │
│  └───────────┬──────────────────▲──────────────────┘     │
│              │ Events           │ State                  │
└──────────────┼──────────────────┼────────────────────────┘
               │                  │
┌──────────────▼──────────────────┼────────────────────────┐
│              │   VIEWMODEL LAYER│                        │
│  ┌───────────┴──────────────────┴──────────────────┐    │
│  │      ReelCounterViewModel.kt                     │    │
│  │  - addReel()                                     │    │
│  │  - resetCounter()                                │    │
│  │  - todayCount: StateFlow<Int>                   │    │
│  │  - Manages UI state                             │    │
│  └───────────┬──────────────────▲──────────────────┘    │
│              │ Data requests    │ Data updates          │
└──────────────┼──────────────────┼────────────────────────┘
               │                  │
┌──────────────▼──────────────────┼────────────────────────┐
│              │  REPOSITORY LAYER│                        │
│  ┌───────────┴──────────────────┴──────────────────┐    │
│  │         ReelRepository.kt                        │    │
│  │  - addReelEntry()                               │    │
│  │  - getTodayCount()                              │    │
│  │  - resetTodayCounter()                          │    │
│  │  - reelEntries: Flow<List<ReelEntry>>          │    │
│  │  - Single source of truth                       │    │
│  └───────────┬──────────────────▲──────────────────┘    │
│              │ CRUD ops         │ Data                  │
└──────────────┼──────────────────┼────────────────────────┘
               │                  │
┌──────────────▼──────────────────┼────────────────────────┐
│              │    DATA LAYER    │                        │
│  ┌───────────┴──────────────────┴──────────────────┐    │
│  │         In-Memory Storage                        │    │
│  │  (Currently MutableStateFlow<List<ReelEntry>>)  │    │
│  │                                                   │    │
│  │  Future: Room Database                           │    │
│  │  - @Entity ReelEntry                            │    │
│  │  - @Dao ReelDao                                 │    │
│  │  - @Database ReelDatabase                       │    │
│  └──────────────────────────────────────────────────┘    │
└───────────────────────────────────────────────────────────┘
```

## State Management Flow

```
                  Unidirectional Data Flow
                          
                          ┌───────┐
                          │  UI   │
                          └───┬───┘
                              │
                    Events    │    State
                    (User     │    (Display)
                    Actions)  │    
                              │
                    ┌─────────▼─────────┐
                    │   ViewModel       │
                    │                   │
                    │  StateFlow<T>     │
                    │  - todayCount     │
                    │  - dailyStats     │
                    └─────────┬─────────┘
                              │
                    Data      │    Updates
                    Requests  │    (Flow)
                              │
                    ┌─────────▼─────────┐
                    │  Repository       │
                    │                   │
                    │  Single Source    │
                    │  of Truth         │
                    └─────────┬─────────┘
                              │
                    CRUD      │    Data
                    Operations│    
                              │
                    ┌─────────▼─────────┐
                    │  Data Storage     │
                    │                   │
                    │  In-Memory / DB   │
                    └───────────────────┘
```

## Lifecycle Flow

```
App Lifecycle Events:

onCreate()
    │
    ├─> MainActivity created
    │   └─> ViewModel initialized
    │       └─> Repository initialized
    │           └─> Data restored (if any)
    │
onStart()
    │
    ├─> UI becomes visible
    │   └─> StateFlow observed
    │       └─> UI shows current state
    │
onResume()
    │
    ├─> User can interact
    │
    │   ┌─────────────────┐
    │   │  User adds reel │
    │   └────────┬────────┘
    │            │
    │   ┌────────▼────────┐
    │   │  Count updates  │
    │   └────────┬────────┘
    │            │
    │   ┌────────▼────────┐
    │   │  UI recomposes  │
    │   └─────────────────┘
    │
onPause()
    │
    ├─> App in background
    │   └─> State preserved in ViewModel
    │
onStop()
    │
    ├─> UI not visible
    │   └─> ViewModel still alive
    │
Configuration Change (e.g., Rotation)
    │
    ├─> Activity destroyed
    │   └─> ViewModel SURVIVES
    │       └─> State preserved
    │
    ├─> Activity recreated
    │   └─> ViewModel reattached
    │       └─> State restored
    │           └─> UI shows same count
    │
onDestroy()
    │
    └─> ViewModel cleared
        └─> Resources cleaned up
```

## Future Enhancement: Room Database

```
When Room is integrated:

┌─────────────────────────────────────────┐
│         ReelRepository                   │
└────────────┬────────────────────────────┘
             │
             ├─> ReelDatabase
             │   └─> ReelDao
             │       ├─> insert()
             │       ├─> getAll()
             │       ├─> getTodayCount()
             │       └─> deleteToday()
             │
             └─> SQLite Database
                 └─> reel_entries table
                     ├─> id (Primary Key)
                     ├─> platform
                     ├─> timestamp
                     └─> date

Benefits:
✓ Persistent storage (survives app restart)
✓ Efficient queries
✓ Type-safe database access
✓ Automatic LiveData/Flow support
```

---

**This diagram shows the complete flow of data and user interactions in the Reel Counter app!** 📊
