package com.reelcounter.app.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
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
        
        // Enable verbose debug logging
        private const val DEBUG_VERBOSE = false
        
        @Volatile
        var isYouTubeActive = false
    }
    
    /**
     * Enum to track which section of YouTube the user is in
     */
    private enum class ViewMode {
        SHORTS,        // User is in YouTube Shorts feed
        NORMAL_FEED,   // User is in normal YouTube feed
        UNKNOWN        // Cannot determine current mode
    }
    
    /**
     * Statistics for monitoring detection accuracy
     */
    private data class DetectionStats(
        var totalScrollEvents: Int = 0,
        var debouncedScrolls: Int = 0,
        var shortsDetected: Int = 0,
        var normalFeedDetected: Int = 0,
        var unknownModeDetected: Int = 0,
        var reelsCounted: Int = 0,
        var errors: Int = 0
    )
    
    private val repository: ReelRepository = ReelRepository.getInstance()
    
    // Coroutine scope for async operations
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    // Track last scroll event time for debouncing
    private var lastScrollEventTime: Long = 0
    
    // Track current view mode
    private var currentViewMode: ViewMode = ViewMode.UNKNOWN
    
    // Detection statistics
    private val stats = DetectionStats()
    
    /**
     * Called when the accessibility service is connected.
     */
    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "YouTube Reel Accessibility Service connected")
        isYouTubeActive = true
        
        // Reset statistics
        resetStats()
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
        if (event.eventType != AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            if (DEBUG_VERBOSE) {
                Log.v(TAG, "Ignoring non-scroll event: ${event.eventType}")
            }
            return
        }
        
        stats.totalScrollEvents++
        
        // Debouncing: Check if enough time has passed since last scroll
        if (!isScrollEventValid()) {
            stats.debouncedScrolls++
            if (DEBUG_VERBOSE) {
                Log.v(TAG, "Scroll event ignored (debouncing) - Total debounced: ${stats.debouncedScrolls}")
            }
            return
        }
        
        // Update last scroll time
        lastScrollEventTime = System.currentTimeMillis()
        
        // Check if we're in Shorts section
        currentViewMode = detectViewMode(event)
        
        // Update statistics
        when (currentViewMode) {
            ViewMode.SHORTS -> stats.shortsDetected++
            ViewMode.NORMAL_FEED -> stats.normalFeedDetected++
            ViewMode.UNKNOWN -> stats.unknownModeDetected++
        }
        
        // Only count if we're in Shorts
        if (currentViewMode == ViewMode.SHORTS) {
            processScrollEvent()
        } else {
            if (DEBUG_VERBOSE) {
                Log.v(TAG, "Scroll detected but not in Shorts section (mode: $currentViewMode)")
            }
        }
        
        // Log statistics periodically (every 10 events)
        if (stats.totalScrollEvents % 10 == 0) {
            logStatistics()
        }
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
     * Detect which section of YouTube the user is currently viewing.
     * 
     * This analyzes the view hierarchy to determine if the user is in Shorts or normal feed.
     * 
     * Detection strategy:
     * - Look for view IDs or content descriptions containing "shorts" or "reel"
     * - Analyze the vertical layout pattern (Shorts use full-screen vertical scrolling)
     * - Check for specific UI components unique to Shorts
     * 
     * @param event The accessibility event to analyze
     * @return The detected view mode
     */
    private fun detectViewMode(event: AccessibilityEvent): ViewMode {
        try {
            val rootNode = rootInActiveWindow
            if (rootNode == null) {
                if (DEBUG_VERBOSE) {
                    Log.v(TAG, "Root node is null, cannot detect view mode")
                }
                return ViewMode.UNKNOWN
            }
            
            // Strategy 1: Check for "shorts" in any node in the hierarchy
            if (containsShortsIndicator(rootNode)) {
                if (DEBUG_VERBOSE) {
                    Log.v(TAG, "Shorts detected via view hierarchy")
                }
                return ViewMode.SHORTS
            }
            
            // Strategy 2: Check the source node's characteristics
            val sourceNode = event.source
            if (sourceNode != null) {
                if (isLikelyShortsScroll(sourceNode)) {
                    if (DEBUG_VERBOSE) {
                        Log.v(TAG, "Shorts detected via scroll characteristics")
                    }
                    return ViewMode.SHORTS
                }
                sourceNode.recycle()
            }
            
            rootNode.recycle()
            
            // Default to UNKNOWN if we can't determine
            if (DEBUG_VERBOSE) {
                Log.v(TAG, "View mode could not be determined")
            }
            return ViewMode.UNKNOWN
            
        } catch (e: Exception) {
            Log.e(TAG, "Error detecting view mode", e)
            stats.errors++
            return ViewMode.UNKNOWN
        }
    }
    
    /**
     * Recursively check if any node contains Shorts-related identifiers.
     * 
     * @param node The node to check
     * @return true if Shorts indicators are found
     */
    private fun containsShortsIndicator(node: AccessibilityNodeInfo): Boolean {
        try {
            // Check view ID
            val viewId = node.viewIdResourceName?.lowercase() ?: ""
            if (viewId.contains("shorts") || viewId.contains("reel")) {
                if (DEBUG_VERBOSE) {
                    Log.v(TAG, "Found Shorts indicator in viewId: $viewId")
                }
                return true
            }
            
            // Check content description
            val contentDesc = node.contentDescription?.toString()?.lowercase() ?: ""
            if (contentDesc.contains("shorts") || contentDesc.contains("short") || contentDesc.contains("reel")) {
                if (DEBUG_VERBOSE) {
                    Log.v(TAG, "Found Shorts indicator in contentDescription: $contentDesc")
                }
                return true
            }
            
            // Check text content
            val text = node.text?.toString()?.lowercase() ?: ""
            if (text.contains("shorts")) {
                if (DEBUG_VERBOSE) {
                    Log.v(TAG, "Found Shorts indicator in text: $text")
                }
                return true
            }
            
            // Recursively check children
            for (i in 0 until node.childCount) {
                val child = node.getChild(i)
                if (child != null) {
                    if (containsShortsIndicator(child)) {
                        child.recycle()
                        return true
                    }
                    child.recycle()
                }
            }
            
            return false
        } catch (e: Exception) {
            Log.e(TAG, "Error checking Shorts indicator", e)
            stats.errors++
            return false
        }
    }
    
    /**
     * Check if the scroll characteristics match Shorts behavior.
     * 
     * Shorts typically have:
     * - Vertical scrolling
     * - Full-screen content
     * - High scroll velocity
     * 
     * @param node The source node of the scroll event
     * @return true if the scroll matches Shorts characteristics
     */
    private fun isLikelyShortsScroll(node: AccessibilityNodeInfo): Boolean {
        try {
            // Check if the scrollable view is full-screen or nearly full-screen
            val bounds = android.graphics.Rect()
            node.getBoundsInScreen(bounds)
            
            // Get screen dimensions
            val displayMetrics = resources.displayMetrics
            val screenHeight = displayMetrics.heightPixels
            val screenWidth = displayMetrics.widthPixels
            
            // Shorts views typically take up most of the screen
            val viewHeight = bounds.height()
            val viewWidth = bounds.width()
            
            val heightRatio = viewHeight.toFloat() / screenHeight
            val widthRatio = viewWidth.toFloat() / screenWidth
            
            if (DEBUG_VERBOSE) {
                Log.v(TAG, "View dimensions: ${viewWidth}x${viewHeight}, Screen: ${screenWidth}x${screenHeight}")
                Log.v(TAG, "Ratios: height=$heightRatio, width=$widthRatio")
            }
            
            // If the view is nearly full-screen (>80% in both dimensions), it's likely Shorts
            if (heightRatio > 0.8 && widthRatio > 0.8) {
                if (DEBUG_VERBOSE) {
                    Log.v(TAG, "Full-screen scroll detected (likely Shorts)")
                }
                return true
            }
            
            return false
        } catch (e: Exception) {
            Log.e(TAG, "Error checking scroll characteristics", e)
            stats.errors++
            return false
        }
    }
    
    /**
     * Process a valid scroll event by incrementing the reel counter.
     */
    private fun processScrollEvent() {
        serviceScope.launch {
            try {
                repository.addReelEntry(platform = "YouTube Shorts")
                stats.reelsCounted++
                val todayCount = repository.getTodayCount()
                Log.d(TAG, "✅ Reel counted! Total today: $todayCount")
            } catch (e: Exception) {
                Log.e(TAG, "Error counting reel", e)
                stats.errors++
            }
        }
    }
    
    /**
     * Log current detection statistics
     */
    private fun logStatistics() {
        val accuracy = if (stats.totalScrollEvents > 0) {
            (stats.shortsDetected.toFloat() / stats.totalScrollEvents * 100).toInt()
        } else {
            0
        }
        
        Log.i(TAG, """
            |📊 Detection Statistics:
            |  Total scroll events: ${stats.totalScrollEvents}
            |  Debounced: ${stats.debouncedScrolls}
            |  Shorts detected: ${stats.shortsDetected}
            |  Normal feed: ${stats.normalFeedDetected}
            |  Unknown: ${stats.unknownModeDetected}
            |  Reels counted: ${stats.reelsCounted}
            |  Errors: ${stats.errors}
            |  Detection accuracy: $accuracy%
        """.trimMargin())
    }
    
    /**
     * Reset statistics
     */
    private fun resetStats() {
        stats.totalScrollEvents = 0
        stats.debouncedScrolls = 0
        stats.shortsDetected = 0
        stats.normalFeedDetected = 0
        stats.unknownModeDetected = 0
        stats.reelsCounted = 0
        stats.errors = 0
        Log.d(TAG, "Statistics reset")
    }
    
    /**
     * Called when the service is interrupted.
     */
    override fun onInterrupt() {
        Log.d(TAG, "YouTube Reel Accessibility Service interrupted")
        logStatistics()
        isYouTubeActive = false
    }
    
    /**
     * Called when the service is disconnected.
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "YouTube Reel Accessibility Service destroyed")
        logStatistics()
        isYouTubeActive = false
        serviceScope.cancel()
    }
}
