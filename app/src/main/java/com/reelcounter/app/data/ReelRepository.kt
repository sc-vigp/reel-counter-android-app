package com.reelcounter.app.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Repository for managing reel count data
 * In a full implementation, this would interact with Room database
 * 
 * Thread-safe: Uses Mutex to prevent race conditions when multiple coroutines
 * call addReelEntry() rapidly from the accessibility service.
 */
class ReelRepository {
    
    private val _reelEntries = MutableStateFlow<List<ReelEntry>>(emptyList())
    val reelEntries: Flow<List<ReelEntry>> = _reelEntries.asStateFlow()
    
    private val _dailyStats = MutableStateFlow<DailyStats?>(null)
    val dailyStats: Flow<DailyStats?> = _dailyStats.asStateFlow()
    
    // Mutex for thread-safe operations on shared state
    private val mutex = Mutex()
    
    /**
     * Add a new reel entry (thread-safe)
     * 
     * This method is called frequently by the accessibility service,
     * potentially from multiple coroutines. The mutex ensures that
     * concurrent calls don't cause data loss.
     */
    suspend fun addReelEntry(platform: String = "Unknown") {
        mutex.withLock {
            val newEntry = ReelEntry(
                id = System.currentTimeMillis(),
                platform = platform
            )
            val currentList = _reelEntries.value.toMutableList()
            currentList.add(newEntry)
            _reelEntries.value = currentList
            updateDailyStats()
        }
    }
    
    /**
     * Get today's reel count (thread-safe read)
     */
    fun getTodayCount(): Int {
        val today = ReelEntry().date
        return _reelEntries.value.count { it.date == today }
    }
    
    /**
     * Reset today's counter (thread-safe)
     */
    suspend fun resetTodayCounter() {
        mutex.withLock {
            val today = ReelEntry().date
            val filteredList = _reelEntries.value.filter { it.date != today }
            _reelEntries.value = filteredList
            updateDailyStats()
        }
    }
    
    /**
     * Update daily statistics
     * 
     * Note: This should only be called from within mutex.withLock blocks
     * to ensure consistency with _reelEntries updates.
     */
    private fun updateDailyStats() {
        val today = ReelEntry().date
        val todayEntries = _reelEntries.value.filter { it.date == today }
        
        if (todayEntries.isNotEmpty()) {
            val platformCounts = todayEntries.groupingBy { it.platform }.eachCount()
            _dailyStats.value = DailyStats(
                date = today,
                totalReels = todayEntries.size,
                platforms = platformCounts
            )
        } else {
            _dailyStats.value = null
        }
    }
    
    companion object {
        @Volatile
        private var instance: ReelRepository? = null
        
        fun getInstance(): ReelRepository {
            return instance ?: synchronized(this) {
                instance ?: ReelRepository().also { instance = it }
            }
        }
    }
}
