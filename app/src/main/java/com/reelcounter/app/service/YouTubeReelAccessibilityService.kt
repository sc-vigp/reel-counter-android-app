package com.reelcounter.app.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.reelcounter.app.data.ReelRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Accessibility Service to detect YouTube Shorts swipes and auto-track them.
 * 
 * This service monitors accessibility events from the YouTube app and automatically
 * increments the reel counter when a scroll/swipe is detected in the Shorts feed.
 */
class YouTubeReelAccessibilityService : AccessibilityService() {
    
    companion object {
        private const val TAG = "YouTubeReelAccessibility"
        private const val YOUTUBE_PACKAGE = "com.google.android.youtube"
        
        // Debouncing: Minimum time between scroll events (milliseconds)
        private const val SCROLL_DEBOUNCE_MS = 500L
        
        @Volatile
        var isYouTubeActive = false
    }
    
    private val repository: ReelRepository = ReelRepository.getInstance()
    
    // Coroutine scope for async operations
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    // Track last scroll event time for debouncing
    private var lastScrollEventTime: Long = 0
    
    /**
     * Called when the accessibility service is connected.
     */
    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "YouTube Reel Accessibility Service connected")
        isYouTubeActive = true
    }
    
    /**
     * Called when an accessibility event is received.
     * 
     * Filters for YouTube scroll events and applies debouncing logic.
     */
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        
        // Filter: Only process YouTube events
        if (event.packageName != YOUTUBE_PACKAGE) return
        
        // Filter: Only process scroll/swipe events
        if (event.eventType != AccessibilityEvent.TYPE_VIEW_SCROLLED) return
        
        // Debouncing: Check if enough time has passed since last scroll
        if (!isScrollEventValid()) {
            Log.d(TAG, "Scroll event ignored (debouncing)")
            return
        }
        
        // Update last scroll time
        lastScrollEventTime = System.currentTimeMillis()
        
        // Process the scroll event
        processScrollEvent()
    }
    
    /**
     * Check if the scroll event should be processed based on debouncing logic.
     * 
     * @return true if enough time has passed since the last scroll event
     */
    private fun isScrollEventValid(): Boolean {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastScroll = currentTime - lastScrollEventTime
        return timeSinceLastScroll >= SCROLL_DEBOUNCE_MS
    }
    
    /**
     * Process a valid scroll event by incrementing the reel counter.
     * 
     * Note: In this phase, we count ALL YouTube scrolls.
     * Phase 3.4 will add Shorts-specific detection to reduce false positives.
     */
    private fun processScrollEvent() {
        serviceScope.launch {
            try {
                repository.addReelEntry(platform = "YouTube Shorts")
                Log.d(TAG, "Reel counted - Total today: ${repository.getTodayCount()}")
            } catch (e: Exception) {
                Log.e(TAG, "Error counting reel", e)
            }
        }
    }
    
    /**
     * Called when the service is interrupted.
     */
    override fun onInterrupt() {
        Log.d(TAG, "YouTube Reel Accessibility Service interrupted")
        isYouTubeActive = false
    }
    
    /**
     * Called when the service is disconnected.
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "YouTube Reel Accessibility Service destroyed")
        isYouTubeActive = false
        serviceScope.cancel()
    }
}
