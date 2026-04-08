package com.example.localmp3player.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.localmp3player.player.AudioPlayerService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying
    
    private val _currentPosition = MutableLiveData(0L)
    val currentPosition: LiveData<Long> = _currentPosition
    
    private val _duration = MutableLiveData(0L)
    val duration: LiveData<Long> = _duration
    
    private val _currentFile = MutableLiveData<String?>(null)
    val currentFile: LiveData<String?> = _currentFile
    
    private val _sleepTimerRemaining = MutableLiveData<Long?>(null)
    val sleepTimerRemaining: LiveData<Long?> = _sleepTimerRemaining
    
    private var sleepTimerJob: kotlinx.coroutines.Job? = null
    
    fun playFile(filePath: String) {
        _currentFile.value = filePath
        _isPlaying.value = true
        
        // Start playback service
        val intent = Intent(getApplication(), AudioPlayerService::class.java).apply {
            action = AudioPlayerService.ACTION_PLAY
            putExtra(AudioPlayerService.EXTRA_FILE_PATH, filePath)
        }
        getApplication<Application>().startService(intent)
        
        // Simulate playback progress (in real app, this would come from service)
        viewModelScope.launch {
            while (_isPlaying.value == true) {
                delay(1000)
                _currentPosition.value = _currentPosition.value?.plus(1000)
            }
        }
    }
    
    fun pause() {
        _isPlaying.value = false
        val intent = Intent(getApplication(), AudioPlayerService::class.java).apply {
            action = AudioPlayerService.ACTION_PAUSE
        }
        getApplication<Application>().startService(intent)
    }
    
    fun resume() {
        _isPlaying.value = true
        val intent = Intent(getApplication(), AudioPlayerService::class.java).apply {
            action = AudioPlayerService.ACTION_PLAY
            putExtra(AudioPlayerService.EXTRA_FILE_PATH, _currentFile.value)
        }
        getApplication<Application>().startService(intent)
    }
    
    fun seekTo(position: Long) {
        _currentPosition.value = position
        // TODO: Send seek command to service
    }
    
    fun setSleepTimer(minutes: Int) {
        sleepTimerJob?.cancel()
        
        val totalSeconds = minutes * 60L
        _sleepTimerRemaining.value = totalSeconds
        
        sleepTimerJob = viewModelScope.launch {
            var remaining = totalSeconds
            while (remaining > 0) {
                delay(1000)
                remaining--
                _sleepTimerRemaining.value = remaining
            }
            
            // Timer finished - stop playback
            _sleepTimerRemaining.value = null
            pause()
        }
    }
    
    fun cancelSleepTimer() {
        sleepTimerJob?.cancel()
        sleepTimerJob = null
        _sleepTimerRemaining.value = null
    }
    
    fun formatTime(milliseconds: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60
        
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
    
    fun formatSeconds(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }
}