package com.reelcounter.app.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.reelcounter.app.data.ReelRepository

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
        
        // Will be used in Phase 3 & 4 for debouncing and detection
        @Volatile
        var isYouTubeActive = false
    }
    
    private val repository: ReelRepository = ReelRepository.getInstance()
    
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
     * Phase 1: Just log that we're receiving events.
     * Phase 3: Implement detection logic.
     * Phase 4: Refine to only count Shorts.
     */
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        
        // Phase 1: Only log for now
        if (event.packageName == YOUTUBE_PACKAGE) {
            Log.d(TAG, "Event from YouTube: ${event.eventType}")
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
    }
}
