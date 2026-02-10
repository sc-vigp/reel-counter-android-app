package com.reelcounter.app.service

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import java.util.concurrent.TimeUnit

/**
 * Detector for monitoring app usage and detecting when specific apps are opened
 */
class AppUsageDetector(private val context: Context) {
    
    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    private var lastCheckedTime = System.currentTimeMillis()
    
    companion object {
        private const val TAG = "AppUsageDetector"
        
        // Package names of social media apps to monitor
        const val INSTAGRAM_PACKAGE = "com.instagram.android"
        const val FACEBOOK_PACKAGE = "com.facebook.katana"
        const val WHATSAPP_PACKAGE = "com.whatsapp"
        
        // Map of package names to display names
        val MONITORED_APPS = mapOf(
            INSTAGRAM_PACKAGE to "Instagram",
            FACEBOOK_PACKAGE to "Facebook",
            WHATSAPP_PACKAGE to "WhatsApp"
        )
    }
    
    /**
     * Check if any monitored apps were opened since last check
     * @return List of app names that were opened
     */
    fun checkForAppOpens(): List<String> {
        val currentTime = System.currentTimeMillis()
        val openedApps = mutableListOf<String>()
        
        try {
            val events = usageStatsManager.queryEvents(lastCheckedTime, currentTime)
            val event = UsageEvents.Event()
            
            while (events.hasNextEvent()) {
                events.getNextEvent(event)
                
                // Check if event is MOVE_TO_FOREGROUND (app opened)
                if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    val packageName = event.packageName
                    
                    // Check if it's one of our monitored apps
                    if (MONITORED_APPS.containsKey(packageName)) {
                        val appName = MONITORED_APPS[packageName] ?: packageName
                        if (!openedApps.contains(appName)) {
                            openedApps.add(appName)
                            Log.d(TAG, "Detected app open: $appName ($packageName)")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking app usage", e)
        }
        
        lastCheckedTime = currentTime
        return openedApps
    }
    
    /**
     * Check if usage stats permission is granted
     */
    fun hasUsageStatsPermission(): Boolean {
        val now = System.currentTimeMillis()
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            now - TimeUnit.DAYS.toMillis(1),
            now
        )
        return stats != null && stats.isNotEmpty()
    }
    
    /**
     * Reset the last checked time (useful when starting the service)
     */
    fun resetLastCheckedTime() {
        lastCheckedTime = System.currentTimeMillis()
    }
}
