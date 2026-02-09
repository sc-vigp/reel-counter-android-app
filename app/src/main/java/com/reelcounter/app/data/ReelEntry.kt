package com.reelcounter.app.data

/**
 * Data class representing a reel view entry
 */
data class ReelEntry(
    val id: Long = 0,
    val platform: String = "Unknown",
    val timestamp: Long = System.currentTimeMillis(),
    val date: String = getCurrentDate()
) {
    companion object {
        private fun getCurrentDate(): String {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            return sdf.format(java.util.Date())
        }
    }
}

/**
 * Data class for daily statistics
 */
data class DailyStats(
    val date: String,
    val totalReels: Int,
    val platforms: Map<String, Int>
)
