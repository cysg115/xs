package com.example.localmp3player.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "playback_history")
data class PlaybackHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val filePath: String,
    val fileName: String,
    val lastPosition: Long, // in milliseconds
    val duration: Long, // total duration in milliseconds
    val playCount: Int = 1,
    val lastPlayed: Long = System.currentTimeMillis(),
    val addedDate: Long = System.currentTimeMillis()
)