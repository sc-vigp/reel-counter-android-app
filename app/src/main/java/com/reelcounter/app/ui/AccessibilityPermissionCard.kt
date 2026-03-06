package com.reelcounter.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Card component to request Accessibility Service permission from the user.
 * 
 * Shows an informative card with explanation and button to enable the service
 * when it's disabled. Shows success state when enabled.
 */
@Composable
fun AccessibilityPermissionCard(
    isEnabled: Boolean,
    onEnableClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon and status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isEnabled) Icons.Filled.Check else Icons.Filled.Warning,
                    contentDescription = if (isEnabled) "Enabled" else "Disabled",
                    tint = if (isEnabled) Color(0xFF2E7D32) else Color(0xFFF57C00),
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = if (isEnabled) "Auto-Tracking Active" else "Enable Auto-Tracking",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isEnabled) Color(0xFF2E7D32) else Color(0xFFF57C00)
                )
            }
            
            // Description
            Text(
                text = if (isEnabled) {
                    "Shorts are being automatically counted in the background."
                } else {
                    "Allow Reel Counter to automatically count your YouTube Shorts. The app will monitor scroll events in YouTube to track your viewing."
                },
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Action button
            if (!isEnabled) {
                Button(
                    onClick = onEnableClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Open Accessibility Settings")
                }
            } else {
                Text(
                    text = "✓ Service is enabled",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Small badge component showing the accessibility service status.
 * 
 * Can be placed in app bar or as a small indicator.
 */
@Composable
fun AccessibilityStatusBadge(
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = if (isEnabled) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isEnabled) Icons.Filled.Check else Icons.Filled.Warning,
            contentDescription = if (isEnabled) "Enabled" else "Disabled",
            tint = if (isEnabled) Color(0xFF2E7D32) else Color(0xFFF57C00),
            modifier = Modifier.size(16.dp)
        )
        
        Spacer(modifier = Modifier.width(6.dp))
        
        Text(
            text = if (isEnabled) "Tracking ON" else "Tracking OFF",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (isEnabled) Color(0xFF2E7D32) else Color(0xFFF57C00)
        )
    }
}
