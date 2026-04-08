package com.example.localmp3player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.localmp3player.player.PlayerScreen
import com.example.localmp3player.playlist.PlaylistScreen
import com.example.localmp3player.settings.SettingsScreen
import com.example.localmp3player.ui.theme.LocalMP3PlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LocalMP3PlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MP3PlayerApp()
                }
            }
        }
    }
}

@Composable
fun MP3PlayerApp() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "player") {
        composable("player") { PlayerScreen(navController) }
        composable("playlist") { PlaylistScreen(navController) }
        composable("settings") { SettingsScreen(navController) }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LocalMP3PlayerTheme {
        MP3PlayerApp()
    }
}