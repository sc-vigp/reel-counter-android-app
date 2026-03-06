package com.reelcounter.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Main screen composable for Reel Counter with accessibility service onboarding
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReelCounterScreen(
    todayCount: Int,
    isAccessibilityServiceEnabled: Boolean,
    onAddReel: () -> Unit,
    onResetCounter: () -> Unit,
    onEnableAccessibility: () -> Unit,
    onCheckAccessibilityStatus: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Check accessibility status when screen first appears
    LaunchedEffect(Unit) {
        onCheckAccessibilityStatus()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reel Counter") },
                actions = {
                    // Show status badge in top bar
                    AccessibilityStatusBadge(
                        isEnabled = isAccessibilityServiceEnabled,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Accessibility Permission Card (shown at top if not enabled)
            if (!isAccessibilityServiceEnabled) {
                AccessibilityPermissionCard(
                    isEnabled = false,
                    onEnableClick = onEnableAccessibility
                )
            } else {
                // Show success state
                AccessibilityPermissionCard(
                    isEnabled = true,
                    onEnableClick = { /* No action when already enabled */ }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Header
            Text(
                text = "Track Your Reel Usage",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Counter Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Total Reels Today",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "$todayCount",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 72.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Text(
                        text = "reels",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Add Reel Button
            Button(
                onClick = onAddReel,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(56.dp)
            ) {
                Text(
                    text = "+ Add Reel",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Reset Button
            OutlinedButton(
                onClick = onResetCounter,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(56.dp)
            ) {
                Text(
                    text = "Reset Counter",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Info Text
            Text(
                text = if (isAccessibilityServiceEnabled) {
                    "Shorts are being automatically counted in the background."
                } else {
                    "Enable accessibility to start automatic tracking."
                },
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
