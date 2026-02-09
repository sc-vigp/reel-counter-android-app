package com.reelcounter.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reelcounter.app.data.DailyStats
import com.reelcounter.app.data.ReelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing reel counter UI state
 */
class ReelCounterViewModel(
    private val repository: ReelRepository = ReelRepository.getInstance()
) : ViewModel() {
    
    private val _todayCount = MutableStateFlow(0)
    val todayCount: StateFlow<Int> = _todayCount.asStateFlow()
    
    private val _dailyStats = MutableStateFlow<DailyStats?>(null)
    val dailyStats: StateFlow<DailyStats?> = _dailyStats.asStateFlow()
    
    init {
        observeReelEntries()
    }
    
    /**
     * Observe reel entries and update count
     */
    private fun observeReelEntries() {
        viewModelScope.launch {
            repository.reelEntries.collect {
                _todayCount.value = repository.getTodayCount()
            }
        }
        
        viewModelScope.launch {
            repository.dailyStats.collect { stats ->
                _dailyStats.value = stats
            }
        }
    }
    
    /**
     * Add a new reel entry
     */
    fun addReel(platform: String = "Unknown") {
        viewModelScope.launch {
            repository.addReelEntry(platform)
        }
    }
    
    /**
     * Reset today's counter
     */
    fun resetCounter() {
        viewModelScope.launch {
            repository.resetTodayCounter()
        }
    }
}
