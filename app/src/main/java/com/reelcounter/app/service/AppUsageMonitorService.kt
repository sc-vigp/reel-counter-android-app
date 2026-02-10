package com.reelcounter.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.reelcounter.app.MainActivity
import com.reelcounter.app.R
import com.reelcounter.app.data.ReelRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Foreground service that monitors app usage and auto-increments counter
 */
class AppUsageMonitorService : Service() {
    
    private lateinit var appUsageDetector: AppUsageDetector
    private lateinit var repository: ReelRepository
    private var monitoringJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.Default)
    
    companion object {
        private const val TAG = "AppUsageMonitorService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "app_usage_monitor_channel"
        private const val CHANNEL_NAME = "App Usage Monitor"
        private const val CHECK_INTERVAL_MS = 2000L // Check every 2 seconds
        
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
        
        appUsageDetector = AppUsageDetector(this)
        repository = ReelRepository.getInstance()
        
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started with action: ${intent?.action}")
        
        when (intent?.action) {
            ACTION_START -> {
                startMonitoring()
            }
            ACTION_STOP -> {
                stopMonitoring()
                stopSelf()
            }
            else -> {
                startMonitoring()
            }
        }
        
        return START_STICKY
    }
    
    private fun startMonitoring() {
        if (monitoringJob?.isActive == true) {
            Log.d(TAG, "Monitoring already active")
            return
        }
        
        // Start as foreground service
        startForeground(NOTIFICATION_ID, createNotification("Monitoring app usage..."))
        
        // Reset the last checked time to avoid counting past opens
        appUsageDetector.resetLastCheckedTime()
        
        // Start monitoring loop
        monitoringJob = serviceScope.launch {
            Log.d(TAG, "Starting monitoring loop")
            
            while (isActive) {
                try {
                    // Check for app opens
                    val openedApps = appUsageDetector.checkForAppOpens()
                    
                    // Increment counter for each app that was opened
                    openedApps.forEach { appName ->
                        Log.d(TAG, "Auto-incrementing counter for: $appName")
                        repository.addReelEntry(appName)
                    }
                    
                    // Update notification with current count
                    if (openedApps.isNotEmpty()) {
                        val count = repository.getTodayCount()
                        updateNotification("Detected ${openedApps.size} app opens. Today: $count reels")
                    }
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Error in monitoring loop", e)
                }
                
                // Wait before next check
                delay(CHECK_INTERVAL_MS)
            }
        }
    }
    
    private fun stopMonitoring() {
        Log.d(TAG, "Stopping monitoring")
        monitoringJob?.cancel()
        monitoringJob = null
        stopForeground(STOP_FOREGROUND_REMOVE)
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitors app usage for reel tracking"
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(message: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Reel Counter Active")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
    
    private fun updateNotification(message: String) {
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, createNotification(message))
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
        stopMonitoring()
    }
}
