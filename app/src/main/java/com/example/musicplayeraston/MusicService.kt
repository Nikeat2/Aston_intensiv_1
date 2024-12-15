package com.example.musicplayeraston

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle

class MusicService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private var playingTrackIndex = 0
    private val trackList = listOf(
        R.raw.cent_song,
        R.raw.kanye_song,
        R.raw.kanye_song2
    )
    private var isPlaying: Boolean = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        mediaPlayer = MediaPlayer.create(this, trackList[playingTrackIndex])
        mediaPlayer.setOnCompletionListener {
            nextTrack()
        }
        restoreState()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showNotification()

        when (intent?.action) {
            ACTION_PLAY -> playMusic()
            ACTION_PAUSE -> pauseMusic()
            ACTION_STOP -> stopMusic()
            ACTION_NEXT -> nextTrack()
            ACTION_PREVIOUS -> previousTrack()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        saveState()
        mediaPlayer.release()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun playMusic() {
        if (!isPlaying) {
            mediaPlayer.start()
            isPlaying = true
        } else {
            mediaPlayer.pause()
            isPlaying = false
            mediaPlayer.start()
            isPlaying = true
        }
    }

    private fun pauseMusic() {
        if (isPlaying) {
            mediaPlayer.pause()
            isPlaying = false
        }
    }

    private fun stopMusic() {
        if (isPlaying) {
            mediaPlayer.stop()
            isPlaying = false
        }
    }

    private fun nextTrack() {
        if (playingTrackIndex < trackList.size - 1) {
            playingTrackIndex++
        } else {
            playingTrackIndex = 0
        }
        changeTrack()
    }

    private fun previousTrack() {
        if (playingTrackIndex > 0) {
            playingTrackIndex--
        } else {
            playingTrackIndex = trackList.size - 1
        }
        changeTrack()
    }

    private fun changeTrack() {
        if (isPlaying) {
            pauseMusic()
            mediaPlayer.release()
            mediaPlayer = MediaPlayer.create(this, trackList[playingTrackIndex])
            playMusic()
        } else {
            pauseMusic()
            mediaPlayer.release()
            mediaPlayer = MediaPlayer.create(this, trackList[playingTrackIndex])
            playMusic()
        }
    }

    private fun saveState() {
        val sharedPreferences = getSharedPreferences("music_service", Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt("playing_track_index", playingTrackIndex)
            .putBoolean("is_playing", isPlaying).apply()
    }

    private fun restoreState() {
        val sharedPreferences = getSharedPreferences("music_service", Context.MODE_PRIVATE)
        playingTrackIndex = sharedPreferences.getInt("playing_track_index", 0)
        isPlaying = sharedPreferences.getBoolean("is_playing", false)
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            "music_service_channel",
            "Music Service Channel",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(serviceChannel)
    }

    private fun showNotification() {
        val playIntent = Intent(this, MusicService::class.java).apply {
            action = ACTION_PLAY
        }

        val playPendingIntent = PendingIntent.getService(
            this, 0, playIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val pauseIntent = Intent(this, MusicService::class.java).apply {
            action = ACTION_PAUSE
        }

        val pausePendingIntent = PendingIntent.getService(
            this, 0, pauseIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val nextTrackIntent = Intent(this, MusicService::class.java).apply {
            action = ACTION_NEXT
        }

        val nextTrackPendingIntent = PendingIntent.getService(
            this, 0, nextTrackIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val previousTrackIntent = Intent(this, MusicService::class.java).apply {
            action = ACTION_PREVIOUS
        }

        val previousTrackPendingIntent =
            PendingIntent.getService(this, 0, previousTrackIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, "music_service_channel")
            .setContentTitle("MusicPlayer")
            .setContentText("Playing now")
            .setSmallIcon(R.drawable.play_sign)
            .addAction(R.drawable.previous_song, "", previousTrackPendingIntent)
            .addAction(R.drawable.pause_sign, "", pausePendingIntent)
            .addAction(R.drawable.play_sign, "", playPendingIntent)
            .addAction(R.drawable.next_song, "", nextTrackPendingIntent)
            .setStyle(MediaStyle())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .build()

        startForeground(1, notification)
    }

    companion object {
        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_NEXT = "ACTION_NEXT"
        const val ACTION_PREVIOUS = "ACTION_PREVIOUS"
    }
}