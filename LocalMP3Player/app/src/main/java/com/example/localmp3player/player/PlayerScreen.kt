package com.example.localmp3player.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.localmp3player.R
import com.example.localmp3player.data.model.PlaybackHistory
import com.example.localmp3player.viewmodel.PlayerViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(navController: NavController) {
    val viewModel: PlayerViewModel = viewModel()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val currentFile by viewModel.currentFile.collectAsState()
    val sleepTimerRemaining by viewModel.sleepTimerRemaining.collectAsState()
    
    val context = LocalContext.current
    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var selectedMinutes by remember { mutableStateOf(15) }
    
    // Auto-play last file on startup
    LaunchedEffect(Unit) {
        val lastPlayback = PlaybackHistoryManager.getLastPlayback(context)
        lastPlayback?.let {
            if (currentFile == null) {
                viewModel.playFile(it.filePath)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Current playing info
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (currentFile != null) {
                        Icon(
                            Icons.Filled.MusicNote,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentFile?.substringAfterLast("/") ?: "Unknown",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = currentFile?.substringBeforeLast("/") ?: "",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    } else {
                        Text("No file playing")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Progress bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = viewModel.formatTime(currentPosition))
                    Text(text = viewModel.formatTime(duration))
                }
                Slider(
                    value = currentPosition.toFloat(),
                    onValueChange = { viewModel.seekTo(it.toLong()) },
                    valueRange = 0f to duration.toFloat(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Playback controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { /* Previous */ },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous")
                }
                
                Spacer(modifier = Modifier.width(32.dp))
                
                IconButton(
                    onClick = {
                        if (isPlaying) viewModel.pause() else viewModel.resume()
                    },
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(48.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(32.dp))
                
                IconButton(
                    onClick = { /* Next */ },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Filled.SkipNext, contentDescription = "Next")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sleep timer
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.sleep_timer),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (sleepTimerRemaining != null) {
                        Text(
                            text = "Timer: ${viewModel.formatSeconds(sleepTimerRemaining!!)} remaining",
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.cancelSleepTimer() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.cancel_timer))
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf(15, 30, 45, 60).forEach { minutes ->
                                Button(
                                    onClick = { viewModel.setSleepTimer(minutes) }
                                ) {
                                    Text("$minutes min")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Recent playbacks
            Text(
                text = stringResource(R.string.recent_playback),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            val recentPlaybacks = remember { PlaybackHistoryManager.getRecentPlaybacks(context) }
            
            if (recentPlaybacks.isNotEmpty()) {
                LazyColumn {
                    items(recentPlaybacks) { history ->
                        RecentPlaybackItem(
                            history = history,
                            onClick = { viewModel.playFile(history.filePath) }
                        )
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.no_music_found),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun RecentPlaybackItem(
    history: PlaybackHistory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.MusicNote,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = history.fileName,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                Text(
                    text = history.filePath.substringBeforeLast("/"),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1
                )
            }
        }
    }
}