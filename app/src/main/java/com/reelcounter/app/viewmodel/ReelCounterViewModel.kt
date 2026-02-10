package com.reelcounter.app.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.reelcounter.app.data.DailyStats
import com.reelcounter.app.data.ReelRepository
import com.reelcounter.app.service.AppUsageMonitorService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing reel counter UI state
 */
class ReelCounterViewModel(
    application: Application,
    private val repository: ReelRepository = ReelRepository.getInstance()
) : AndroidViewModel(application) {
    
    private val _todayCount = MutableStateFlow(0)
    val todayCount: StateFlow<Int> = _todayCount.asStateFlow()
    
    private val _dailyStats = MutableStateFlow<DailyStats?>(null)
    val dailyStats: StateFlow<DailyStats?> = _dailyStats.asStateFlow()
    
    private val _isMonitoring = MutableStateFlow(false)
    val isMonitoring: StateFlow<Boolean> = _isMonitoring.asStateFlow()
    
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
    
    /**
     * Start the app usage monitoring service
     */
    fun startMonitoring() {
        val intent = Intent(getApplication(), AppUsageMonitorService::class.java).apply {
            action = AppUsageMonitorService.ACTION_START
        }
        getApplication<Application>().startService(intent)
        _isMonitoring.value = true
    }
    
    /**
     * Stop the app usage monitoring service
     */
    fun stopMonitoring() {
        val intent = Intent(getApplication(), AppUsageMonitorService::class.java).apply {
            action = AppUsageMonitorService.ACTION_STOP
        }
        getApplication<Application>().startService(intent)
        _isMonitoring.value = false
    }
}
