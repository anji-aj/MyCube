package com.example.mycube.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mycube.ui.viewmodel.StatsViewModel
import com.example.mycube.utils.formatTime

@Composable
fun StatsScreen(viewModel: StatsViewModel = hiltViewModel()) {
    val solves by viewModel.solves.collectAsState()
    val bestTime by viewModel.bestTime.collectAsState()
    val ao5 by viewModel.averageOf5.collectAsState()
    val ao12 by viewModel.averageOf12.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("STATISTICS", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            StatCard("Best", bestTime?.let { formatTime(it) } ?: "--:--")
            StatCard("Ao5", ao5?.let { formatTime(it) } ?: "--:--")
            StatCard("Ao12", ao12?.let { formatTime(it) } ?: "--:--")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("HISTORY", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(solves) { solve ->
                ListItem(
                    headlineContent = { Text(formatTime(solve.timeMillis)) },
                    supportingContent = { Text("Moves: ${solve.movesCount} | ${java.text.SimpleDateFormat("MMM dd, HH:mm").format(java.util.Date(solve.timestamp))}") },
                    trailingContent = { Text("${solve.cubeSize}x${solve.cubeSize}") }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String) {
    Card(modifier = Modifier.padding(4.dp)) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}
