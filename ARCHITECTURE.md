# Architecture Guide

This document explains the architecture and design patterns used in the Reel Counter app.

## MVVM Architecture

The app follows the **Model-View-ViewModel (MVVM)** architecture pattern, which is recommended by Google for Android apps.

```
┌─────────────┐
│     View    │  (UI Layer - Composables)
│  (Screen)   │  - ReelCounterScreen.kt
└──────┬──────┘  - Observes state
       │         - Displays UI
       │         - Sends user actions
       ↓
┌─────────────┐
│  ViewModel  │  (Presentation Layer)
│             │  - ReelCounterViewModel.kt
└──────┬──────┘  - Manages UI state (StateFlow)
       │         - Handles business logic
       │         - Processes user actions
       ↓
┌─────────────┐
│ Repository  │  (Data Layer)
│             │  - ReelRepository.kt
└──────┬──────┘  - Single source of truth
       │         - Data operations
       │         - Can switch data sources
       ↓
┌─────────────┐
│    Model    │  (Data Models)
│             │  - ReelEntry.kt
└─────────────┘  - Data structures
```

## Layer Details

### 1. View Layer (UI)

**Location**: `app/src/main/java/com/reelcounter/app/ui/`

**Purpose**: Display UI and handle user interactions

**Components**:
- `ReelCounterScreen.kt`: Main Compose UI
- `theme/`: App theming (colors, typography)

**Key Concepts**:
- Uses Jetpack Compose (declarative UI)
- Observes ViewModel state via `collectAsState()`
- Reacts to state changes automatically
- Sends user actions to ViewModel

**Example**:
```kotlin
@Composable
fun ReelCounterScreen(
    todayCount: Int,                  // State from ViewModel
    onAddReel: () -> Unit,            // Action to ViewModel
    onResetCounter: () -> Unit        // Action to ViewModel
) {
    // UI components that react to state
}
```

### 2. ViewModel Layer

**Location**: `app/src/main/java/com/reelcounter/app/viewmodel/`

**Purpose**: Manage UI state and business logic

**Components**:
- `ReelCounterViewModel.kt`: Main ViewModel

**Key Concepts**:
- Extends `ViewModel` (lifecycle-aware)
- Uses `StateFlow` for reactive state
- Survives configuration changes (rotation)
- Uses `viewModelScope` for coroutines

**Responsibilities**:
- Fetch data from Repository
- Transform data for UI
- Handle user actions
- Manage loading/error states

**Example**:
```kotlin
class ReelCounterViewModel : ViewModel() {
    private val _todayCount = MutableStateFlow(0)
    val todayCount: StateFlow<Int> = _todayCount.asStateFlow()
    
    fun addReel() {
        viewModelScope.launch {
            repository.addReelEntry()
        }
    }
}
```

### 3. Repository Layer

**Location**: `app/src/main/java/com/reelcounter/app/data/`

**Purpose**: Manage data operations

**Components**:
- `ReelRepository.kt`: Data repository
- Uses singleton pattern

**Key Concepts**:
- Single source of truth
- Abstracts data sources
- Can switch between local/remote data
- Uses Kotlin Flow for reactive data

**Responsibilities**:
- CRUD operations
- Data caching
- Coordinate between data sources
- Business logic for data

**Current Implementation**:
```kotlin
class ReelRepository {
    private val _reelEntries = MutableStateFlow<List<ReelEntry>>(emptyList())
    
    suspend fun addReelEntry(platform: String) {
        // Add entry to list
    }
    
    fun getTodayCount(): Int {
        // Calculate today's count
    }
}
```

### 4. Model Layer

**Location**: `app/src/main/java/com/reelcounter/app/data/`

**Purpose**: Define data structures

**Components**:
- `ReelEntry.kt`: Data classes

**Key Concepts**:
- Kotlin data classes
- Immutable by default
- Simple, clean structure

**Example**:
```kotlin
data class ReelEntry(
    val id: Long = 0,
    val platform: String = "Unknown",
    val timestamp: Long = System.currentTimeMillis(),
    val date: String = getCurrentDate()
)
```

## Data Flow

### User Adds a Reel

```
User taps "Add Reel" button
        ↓
ReelCounterScreen calls onAddReel()
        ↓
ReelCounterViewModel.addReel() invoked
        ↓
ViewModel calls repository.addReelEntry()
        ↓
Repository adds entry to list
        ↓
Repository updates StateFlow
        ↓
ViewModel observes the change
        ↓
ViewModel updates todayCount StateFlow
        ↓
UI observes todayCount change
        ↓
UI automatically recomposes with new count
```

## Design Patterns

### 1. Observer Pattern
- UI observes ViewModel state
- ViewModel observes Repository data
- Implemented via Kotlin Flow

### 2. Singleton Pattern
- Repository uses singleton
- Ensures single data source

### 3. Unidirectional Data Flow
- Data flows down (ViewModel → UI)
- Events flow up (UI → ViewModel)

### 4. Separation of Concerns
- Each layer has specific responsibility
- Easy to test independently

## State Management

### StateFlow vs LiveData

This app uses **StateFlow** (modern approach):

**Advantages**:
- Kotlin-first (coroutines support)
- Better in Compose
- Type-safe
- Lifecycle-aware with `collectAsState()`

**Example**:
```kotlin
// In ViewModel
private val _state = MutableStateFlow(0)
val state: StateFlow<Int> = _state.asStateFlow()

// In Composable
val count by viewModel.state.collectAsState()
```

## Dependency Injection

Currently uses **manual DI** (simple approach for learning):

```kotlin
class ReelCounterViewModel(
    private val repository: ReelRepository = ReelRepository.getInstance()
) : ViewModel()
```

**Future Enhancement**: Can migrate to Hilt for automatic DI.

## Testing Strategy

### Unit Tests
- Test ViewModels in isolation
- Test Repository logic
- Test data transformations

### UI Tests
- Test Composables
- Test user interactions
- Test navigation

**Example**:
```kotlin
@Test
fun `when add reel clicked, count increases`() {
    // Given
    val viewModel = ReelCounterViewModel()
    
    // When
    viewModel.addReel()
    
    // Then
    assertEquals(1, viewModel.todayCount.value)
}
```

## Best Practices Implemented

✅ **Separation of Concerns**: Each layer has one job  
✅ **Single Responsibility**: Classes do one thing well  
✅ **Testability**: Easy to test each layer  
✅ **Immutability**: Data classes are immutable  
✅ **Type Safety**: Kotlin's null safety  
✅ **Lifecycle Awareness**: ViewModel survives config changes  
✅ **Reactive UI**: UI updates automatically  

## Future Enhancements

### Phase 1: Persistent Storage
```
Repository
    ↓
Room Database ← Local SQLite
```

### Phase 2: Remote Data
```
Repository
    ↓
    ├─→ Room Database (cache)
    └─→ Remote API (cloud sync)
```

### Phase 3: Dependency Injection
```
@HiltViewModel
class ReelCounterViewModel @Inject constructor(
    private val repository: ReelRepository
) : ViewModel()
```

## Learning Resources

- [Guide to app architecture](https://developer.android.com/topic/architecture)
- [ViewModel overview](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [StateFlow and SharedFlow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow)
- [Jetpack Compose state](https://developer.android.com/jetpack/compose/state)

## Summary

This architecture provides:
- **Scalability**: Easy to add features
- **Maintainability**: Clear code organization
- **Testability**: Each layer can be tested
- **Flexibility**: Easy to swap implementations
- **Best Practices**: Industry-standard patterns

As you build on this template, maintain these architectural principles for a clean, professional Android app! 🏗️
