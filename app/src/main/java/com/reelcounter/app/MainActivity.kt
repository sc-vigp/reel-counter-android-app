package com.reelcounter.app

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.reelcounter.app.service.AppUsageDetector
import com.reelcounter.app.ui.ReelCounterScreen
import com.reelcounter.app.ui.theme.ReelCounterTheme
import com.reelcounter.app.viewmodel.ReelCounterViewModel

/**
 * Main Activity for the Reel Counter app
 */
class MainActivity : ComponentActivity() {
    
    private lateinit var viewModel: ReelCounterViewModel
    private lateinit var appUsageDetector: AppUsageDetector
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[ReelCounterViewModel::class.java]
        
        // Initialize app usage detector
        appUsageDetector = AppUsageDetector(this)
        
        enableEdgeToEdge()
        
        setContent {
            ReelCounterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ReelCounterApp(
                        viewModel = viewModel,
                        hasUsageStatsPermission = appUsageDetector.hasUsageStatsPermission(),
                        onRequestPermission = { requestUsageStatsPermission() }
                    )
                }
            }
        }
    }
    
    private fun requestUsageStatsPermission() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        startActivity(intent)
    }
}

@Composable
fun ReelCounterApp(
    viewModel: ReelCounterViewModel,
    hasUsageStatsPermission: Boolean,
    onRequestPermission: () -> Unit
) {
    val todayCount by viewModel.todayCount.collectAsState()
    val isMonitoring by viewModel.isMonitoring.collectAsState()
    
    ReelCounterScreen(
        todayCount = todayCount,
        isMonitoring = isMonitoring,
        hasUsageStatsPermission = hasUsageStatsPermission,
        onAddReel = { viewModel.addReel() },
        onResetCounter = { viewModel.resetCounter() },
        onStartMonitoring = { viewModel.startMonitoring() },
        onStopMonitoring = { viewModel.stopMonitoring() },
        onRequestPermission = onRequestPermission
    )
}
