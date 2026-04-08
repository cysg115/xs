package com.example.localmp3player.player

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

object PlaybackHistoryManager {
    private lateinit var database: com.example.localmp3player.data.database.AppDatabase
    
    fun initialize(context: Context) {
        database = com.example.localmp3player.data.database.AppDatabase.getDatabase(context)
    }
    
    fun savePlayback(context: Context, filePath: String, position: Long) {
        if (!::database.isInitialized) {
            initialize(context)
        }
        
        CoroutineScope(Dispatchers.IO).launch {
            val file = File(filePath)
            val fileName = file.name
            val duration = 0L // TODO: Get actual duration from player
            
            val existing = database.playbackHistoryDao().getByFilePath(filePath)
            
            if (existing != null) {
                // Update existing record
                val updated = existing.copy(
                    lastPosition = position,
                    playCount = existing.playCount + 1,
                    lastPlayed = System.currentTimeMillis()
                )
                database.playbackHistoryDao().update(updated)
            } else {
                // Insert new record
                val history = com.example.localmp3player.data.model.PlaybackHistory(
                    filePath = filePath,
                    fileName = fileName,
                    lastPosition = position,
                    duration = duration
                )
                database.playbackHistoryDao().insert(history)
            }
        }
    }
    
    fun getRecentPlaybacks(context: Context): List<com.example.localmp3player.data.model.PlaybackHistory> {
        if (!::database.isInitialized) {
            initialize(context)
        }
        
        // Note: This should be called from a coroutine or LiveData/Flow
        // For simplicity, we're using runBlocking here (not recommended for production)
        return runCatching {
            kotlinx.coroutines.runBlocking {
                database.playbackHistoryDao().getRecent()
            }
        }.getOrNull() ?: emptyList()
    }
    
    fun getLastPlayback(context: Context): com.example.localmp3player.data.model.PlaybackHistory? {
        if (!::database.isInitialized) {
            initialize(context)
        }
        
        return runCatching {
            kotlinx.coroutines.runBlocking {
                database.playbackHistoryDao().getRecent().firstOrNull()
            }
        }.getOrNull()
    }
}