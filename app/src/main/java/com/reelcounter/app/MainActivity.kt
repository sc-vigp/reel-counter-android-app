package com.reelcounter.app

import android.os.Bundle
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
import com.reelcounter.app.ui.ReelCounterScreen
import com.reelcounter.app.ui.theme.ReelCounterTheme
import com.reelcounter.app.viewmodel.ReelCounterViewModel

/**
 * Main Activity for the Reel Counter app
 */
class MainActivity : ComponentActivity() {
    
    private lateinit var viewModel: ReelCounterViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize ViewModel with application context
        viewModel = ReelCounterViewModel(context = this)
        
        enableEdgeToEdge()
        
        setContent {
            ReelCounterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ReelCounterApp(viewModel = viewModel)
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Check accessibility service status when activity resumes
        // This allows us to detect if user manually enabled/disabled service in Settings
        viewModel.checkAccessibilityServiceStatus()
    }
}

@Composable
fun ReelCounterApp(viewModel: ReelCounterViewModel) {
    val todayCount by viewModel.todayCount.collectAsState()
    val isAccessibilityServiceEnabled by viewModel.isAccessibilityServiceEnabled.collectAsState()
    
    ReelCounterScreen(
        todayCount = todayCount,
        isAccessibilityServiceEnabled = isAccessibilityServiceEnabled,
        onAddReel = { viewModel.addReel() },
        onResetCounter = { viewModel.resetCounter() },
        onEnableAccessibility = { viewModel.openAccessibilitySettings() },
        onCheckAccessibilityStatus = { viewModel.checkAccessibilityServiceStatus() }
    )
}
