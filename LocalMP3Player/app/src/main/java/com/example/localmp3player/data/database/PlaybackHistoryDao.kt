package com.example.localmp3player.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.localmp3player.data.model.PlaybackHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaybackHistoryDao {
    @Query("SELECT * FROM playback_history ORDER BY lastPlayed DESC")
    fun getAll(): Flow<List<PlaybackHistory>>

    @Query("SELECT * FROM playback_history WHERE filePath = :filePath")
    suspend fun getByFilePath(filePath: String): PlaybackHistory?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(history: PlaybackHistory): Long

    @Update
    suspend fun update(history: PlaybackHistory)

    @Query("DELETE FROM playback_history WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM playback_history ORDER BY lastPlayed DESC LIMIT 10")
    fun getRecent(): Flow<List<PlaybackHistory>>

    @Query("UPDATE playback_history SET lastPosition = :position, lastPlayed = :timestamp WHERE filePath = :filePath")
    suspend fun updatePosition(filePath: String, position: Long, timestamp: Long = System.currentTimeMillis())
}