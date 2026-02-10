package com.reelcounter.app.service

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for AppUsageDetector constants and mappings
 */
class AppUsageDetectorTest {
    
    @Test
    fun monitoredApps_containsInstagram() {
        assertTrue(AppUsageDetector.MONITORED_APPS.containsKey(AppUsageDetector.INSTAGRAM_PACKAGE))
        assertEquals("Instagram", AppUsageDetector.MONITORED_APPS[AppUsageDetector.INSTAGRAM_PACKAGE])
    }
    
    @Test
    fun monitoredApps_containsFacebook() {
        assertTrue(AppUsageDetector.MONITORED_APPS.containsKey(AppUsageDetector.FACEBOOK_PACKAGE))
        assertEquals("Facebook", AppUsageDetector.MONITORED_APPS[AppUsageDetector.FACEBOOK_PACKAGE])
    }
    
    @Test
    fun monitoredApps_containsWhatsApp() {
        assertTrue(AppUsageDetector.MONITORED_APPS.containsKey(AppUsageDetector.WHATSAPP_PACKAGE))
        assertEquals("WhatsApp", AppUsageDetector.MONITORED_APPS[AppUsageDetector.WHATSAPP_PACKAGE])
    }
    
    @Test
    fun monitoredApps_hasCorrectPackageNames() {
        assertEquals("com.instagram.android", AppUsageDetector.INSTAGRAM_PACKAGE)
        assertEquals("com.facebook.katana", AppUsageDetector.FACEBOOK_PACKAGE)
        assertEquals("com.whatsapp", AppUsageDetector.WHATSAPP_PACKAGE)
    }
    
    @Test
    fun monitoredApps_countIsCorrect() {
        assertEquals(3, AppUsageDetector.MONITORED_APPS.size)
    }
}
