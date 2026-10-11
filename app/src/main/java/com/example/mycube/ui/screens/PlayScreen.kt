package com.example.mycube.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mycube.ui.cube.CubeView
import com.example.mycube.ui.viewmodel.PlayViewModel
import com.example.mycube.utils.formatTime

@Composable
fun PlayScreen(viewModel: PlayViewModel = hiltViewModel()) {
    val timeMillis by viewModel.timeMillis.collectAsState()
    val scramble by viewModel.scramble.collectAsState()
    val moveCount by viewModel.moveCount.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Scramble
            Text(
                text = scramble,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontFamily = FontFamily.Monospace
            )

            // Timer and Move Count
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("TIME", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(
                        text = formatTime(timeMillis),
                        fontSize = 28.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("MOVES", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(
                        text = moveCount.toString(),
                        fontSize = 28.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // 3D Cube View - Centered and Large
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CubeView(viewModel = viewModel)
            }

            // Controls - Minimalist
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { viewModel.undo() }) {
                    Text("Undo", fontSize = 18.sp)
                }
                
                IconButton(
                    onClick = { viewModel.generateNewScramble() },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        Icons.Default.Refresh, 
                        contentDescription = "New Scramble",
                        modifier = Modifier.size(32.dp)
                    )
                }

                TextButton(onClick = { viewModel.redo() }) {
                    Text("Redo", fontSize = 18.sp)
                }
            }
        }
    }
}
