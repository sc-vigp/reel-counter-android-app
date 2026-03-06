package com.reelcounter.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reelcounter.app.data.DailyStats
import com.reelcounter.app.data.ReelRepository
import com.reelcounter.app.service.YouTubeReelAccessibilityService
import com.reelcounter.app.utils.AccessibilityServiceUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing reel counter UI state and accessibility service status
 */
class ReelCounterViewModel(
    private val repository: ReelRepository = ReelRepository.getInstance(),
    private val context: Context? = null
) : ViewModel() {
    
    private val _todayCount = MutableStateFlow(0)
    val todayCount: StateFlow<Int> = _todayCount.asStateFlow()
    
    private val _dailyStats = MutableStateFlow<DailyStats?>(null)
    val dailyStats: StateFlow<DailyStats?> = _dailyStats.asStateFlow()
    
    private val _isAccessibilityServiceEnabled = MutableStateFlow(false)
    val isAccessibilityServiceEnabled: StateFlow<Boolean> = _isAccessibilityServiceEnabled.asStateFlow()
    
    init {
        observeReelEntries()
        checkAccessibilityServiceStatus()
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
     * Check if the Accessibility Service is currently enabled
     */
    fun checkAccessibilityServiceStatus() {
        if (context == null) return
        
        // Use both methods to be sure, as AccessibilityManager can sometimes be laggy 
        // in updating its list right after the user returns from settings.
        val isEnabled = AccessibilityServiceUtils.isAccessibilityServiceEnabled(
            context,
            YouTubeReelAccessibilityService::class.java
        ) || AccessibilityServiceUtils.isAccessibilityServiceEnabledSecure(
            context,
            YouTubeReelAccessibilityService::class.java
        )

        _isAccessibilityServiceEnabled.value = isEnabled
    }
    
    /**
     * Open Android Accessibility Settings for the user to enable the service
     */
    fun openAccessibilitySettings() {
        if (context == null) return
        AccessibilityServiceUtils.openAccessibilitySettings(context)
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
