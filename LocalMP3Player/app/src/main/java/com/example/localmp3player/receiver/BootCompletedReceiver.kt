package com.example.localmp3player.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.example.localmp3player.player.AudioPlayerService
import com.example.localmp3player.player.PlaybackHistoryManager

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED, "android.intent.action.QUICKBOOT_POWERON" -> {
                // Check if auto-play is enabled
                val prefs = PreferenceManager.getDefaultSharedPreferences(context)
                val autoPlay = prefs.getBoolean("auto_play_on_startup", true)
                
                if (autoPlay) {
                    // Get last played file
                    val lastPlayback = PlaybackHistoryManager.getLastPlayback(context)
                    lastPlayback?.let { history ->
                        // Start playback service
                        val serviceIntent = Intent(context, AudioPlayerService::class.java).apply {
                            action = AudioPlayerService.ACTION_PLAY
                            putExtra(AudioPlayerService.EXTRA_FILE_PATH, history.filePath)
                        }
                        context.startService(serviceIntent)
                    }
                }
            }
        }
    }
}