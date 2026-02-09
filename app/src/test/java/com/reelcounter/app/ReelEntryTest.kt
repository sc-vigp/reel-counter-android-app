package com.reelcounter.app

import com.reelcounter.app.data.ReelEntry
import org.junit.Assert.*
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

/**
 * Example unit test for ReelEntry data class
 * 
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ReelEntryTest {
    
    @Test
    fun reelEntry_createsWithCurrentDate() {
        val entry = ReelEntry(platform = "Instagram")
        val expectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        
        assertEquals(expectedDate, entry.date)
    }
    
    @Test
    fun reelEntry_hasDefaultValues() {
        val entry = ReelEntry()
        
        assertEquals(0L, entry.id)
        assertEquals("Unknown", entry.platform)
        assertNotNull(entry.timestamp)
        assertNotNull(entry.date)
    }
    
    @Test
    fun reelEntry_customPlatform() {
        val platforms = listOf("Instagram", "TikTok", "YouTube", "Facebook")
        
        platforms.forEach { platform ->
            val entry = ReelEntry(platform = platform)
            assertEquals(platform, entry.platform)
        }
    }
}
