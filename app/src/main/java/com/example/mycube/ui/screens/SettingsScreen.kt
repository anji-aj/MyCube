package com.example.mycube.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("SETTINGS", style = MaterialTheme.typography.headlineMedium)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        var soundEnabled by remember { mutableStateOf(true) }
        var hapticsEnabled by remember { mutableStateOf(true) }
        var darkMode by remember { mutableStateOf(true) }

        ListItem(
            headlineContent = { Text("Sound Effects") },
            trailingContent = { Switch(checked = soundEnabled, onCheckedChange = { soundEnabled = it }) }
        )
        ListItem(
            headlineContent = { Text("Vibration") },
            trailingContent = { Switch(checked = hapticsEnabled, onCheckedChange = { hapticsEnabled = it }) }
        )
        ListItem(
            headlineContent = { Text("Dark Mode") },
            trailingContent = { Switch(checked = darkMode, onCheckedChange = { darkMode = it }) }
        )
        
        HorizontalDivider()
        
        Text("About", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 16.dp))
        Text("Version 1.0.0")
        Text("Professional Rubik's Cube Trainer")
    }
}
