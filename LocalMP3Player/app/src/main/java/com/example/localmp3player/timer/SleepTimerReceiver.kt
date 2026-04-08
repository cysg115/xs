package com.example.localmp3player.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.localmp3player.player.AudioPlayerService

class SleepTimerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_SLEEP_TIMER -> {
                // Stop playback when sleep timer expires
                val stopIntent = Intent(context, AudioPlayerService::class.java).apply {
                    action = AudioPlayerService.ACTION_PAUSE
                }
                context.startService(stopIntent)
            }
        }
    }

    companion object {
        const val ACTION_SLEEP_TIMER = "com.example.localmp3player.ACTION_SLEEP_TIMER"
    }
}