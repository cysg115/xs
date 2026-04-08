package com.example.localmp3player.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.localmp3player.MainActivity
import com.example.localmp3player.R

class AudioPlayerService : Service() {
    private val binder = LocalBinder()
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var audioManager: AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null
    private var currentMediaItem: MediaItem? = null
    
    private val notificationId = 1
    private val channelId = "audio_player_channel"
    
    inner class LocalBinder : Binder() {
        fun getService(): AudioPlayerService = this@AudioPlayerService
    }

    override fun onCreate() {
        super.onCreate()
        initializePlayer()
        createNotificationChannel()
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY -> {
                val filePath = intent.getStringExtra(EXTRA_FILE_PATH)
                filePath?.let { playFile(it) }
            }
            ACTION_PAUSE -> pause()
            ACTION_STOP -> stopSelf()
            ACTION_NEXT -> playNext()
            ACTION_PREVIOUS -> playPrevious()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }

    private fun initializePlayer() {
        exoPlayer = ExoPlayer.Builder(this).build().apply {
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_ENDED -> {
                            // Auto play next track
                            playNext()
                        }
                        Player.STATE_READY -> {
                            // Update notification
                            updateNotification()
                        }
                    }
                }
            })
        }
    }

    private fun releasePlayer() {
        exoPlayer.release()
        abandonAudioFocus()
    }

    fun playFile(filePath: String) {
        if (requestAudioFocus()) {
            currentMediaItem = MediaItem.fromUri("file://$filePath")
            exoPlayer.setMediaItem(currentMediaItem!!)
            exoPlayer.prepare()
            exoPlayer.play()
            startForegroundService()
            
            // Save playback history
            PlaybackHistoryManager.savePlayback(this, filePath, exoPlayer.currentPosition)
        }
    }

    fun pause() {
        exoPlayer.pause()
        updateNotification()
        abandonAudioFocus()
    }

    fun resume() {
        if (requestAudioFocus()) {
            exoPlayer.play()
            updateNotification()
        }
    }

    fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
    }

    fun getCurrentPosition(): Long = exoPlayer.currentPosition
    fun getDuration(): Long = exoPlayer.duration
    fun isPlaying(): Boolean = exoPlayer.isPlaying

    private fun playNext() {
        // TODO: Implement playlist next track
    }

    private fun playPrevious() {
        // TODO: Implement playlist previous track
    }

    private fun requestAudioFocus(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener { focusChange ->
                    when (focusChange) {
                        AudioManager.AUDIOFOCUS_GAIN -> resume()
                        AudioManager.AUDIOFOCUS_LOSS -> pause()
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> pause()
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                            exoPlayer.volume = 0.1f
                        }
                    }
                }
                .build()
            
            audioManager.requestAudioFocus(audioFocusRequest!!) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                { focusChange ->
                    when (focusChange) {
                        AudioManager.AUDIOFOCUS_GAIN -> resume()
                        AudioManager.AUDIOFOCUS_LOSS -> pause()
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> pause()
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                            exoPlayer.volume = 0.1f
                        }
                    }
                },
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
    }

    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(null)
        }
    }

    private fun startForegroundService() {
        val notification = buildNotification()
        startForeground(notificationId, notification)
    }

    private fun buildNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build play/pause action
        val playPauseAction = if (isPlaying()) {
            NotificationCompat.Action(
                R.drawable.ic_pause,
                getString(R.string.pause),
                getPendingIntent(ACTION_PAUSE)
            )
        } else {
            NotificationCompat.Action(
                R.drawable.ic_play,
                getString(R.string.play),
                getPendingIntent(ACTION_PLAY)
            )
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(if (isPlaying()) "Playing" else "Paused")
            .setSmallIcon(R.drawable.ic_music_note)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(
                NotificationCompat.Action(
                    R.drawable.ic_skip_previous,
                    getString(R.string.previous),
                    getPendingIntent(ACTION_PREVIOUS)
                )
            )
            .addAction(playPauseAction)
            .addAction(
                NotificationCompat.Action(
                    R.drawable.ic_skip_next,
                    getString(R.string.next),
                    getPendingIntent(ACTION_NEXT)
                )
            )
            .build()
    }

    private fun getPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, AudioPlayerService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun updateNotification() {
        val notification = buildNotification()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Audio Player",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Audio playback controls"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val ACTION_PLAY = "com.example.localmp3player.ACTION_PLAY"
        const val ACTION_PAUSE = "com.example.localmp3player.ACTION_PAUSE"
        const val ACTION_STOP = "com.example.localmp3player.ACTION_STOP"
        const val ACTION_NEXT = "com.example.localmp3player.ACTION_NEXT"
        const val ACTION_PREVIOUS = "com.example.localmp3player.ACTION_PREVIOUS"
        const val EXTRA_FILE_PATH = "file_path"
    }
}