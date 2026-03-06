package com.reelcounter.app.utils

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.accessibility.AccessibilityManager

/**
 * Utility functions for managing the Accessibility Service.
 * 
 * Provides helpers to check if the service is enabled and to open
 * Android's Accessibility Settings for the user to enable/disable it.
 */
object AccessibilityServiceUtils {
    
    private const val ACCESSIBILITY_SETTINGS_ACTION = "android.settings.ACCESSIBILITY_SETTINGS"
    
    /**
     * Check if the YouTube Reel Accessibility Service is enabled.
     * 
     * @param context Android context
     * @param serviceClass The accessibility service class to check for
     * @return true if the service is enabled in accessibility settings, false otherwise
     */
    fun isAccessibilityServiceEnabled(context: Context, serviceClass: Class<*>): Boolean {
        val expectedId = "${context.packageName}/${serviceClass.canonicalName}"
        
        return try {
            val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
            val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(
                AccessibilityServiceInfo.FEEDBACK_GENERIC
            )
            
            // Check if our service is in the enabled list
            // Log for debugging if needed: println("Checking for $expectedId in ${enabledServices.map { it.id }}")
            enabledServices.any { it.id == expectedId || it.id.contains(expectedId) }
        } catch (e: Exception) {
            // If any error occurs, assume not enabled
            false
        }
    }
    
    /**
     * Check if the accessibility service is enabled using Settings.Secure.
     * This is sometimes more reliable than AccessibilityManager.
     */
    fun isAccessibilityServiceEnabledSecure(context: Context, serviceClass: Class<*>): Boolean {
        val expectedId = "${context.packageName}/${serviceClass.canonicalName}"
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        
        return enabledServices.split(':').any { it.equals(expectedId, ignoreCase = true) }
    }
    
    /**
     * Open Android Accessibility Settings to allow user to enable this service.
     * 
     * @param context Android context used to start the settings activity
     */
    fun openAccessibilitySettings(context: Context) {
        try {
            val intent = Intent(ACCESSIBILITY_SETTINGS_ACTION)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: Exception) {
            // If accessibility settings can't be opened, open general settings
            try {
                val fallbackIntent = Intent(Settings.ACTION_SETTINGS)
                fallbackIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(fallbackIntent)
            } catch (ex: Exception) {
                // Silently fail - user's device doesn't support opening settings
            }
        }
    }
}
