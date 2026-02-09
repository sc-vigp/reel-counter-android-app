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
        
        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[ReelCounterViewModel::class.java]
        
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
}

@Composable
fun ReelCounterApp(viewModel: ReelCounterViewModel) {
    val todayCount by viewModel.todayCount.collectAsState()
    
    ReelCounterScreen(
        todayCount = todayCount,
        onAddReel = { viewModel.addReel() },
        onResetCounter = { viewModel.resetCounter() }
    )
}
