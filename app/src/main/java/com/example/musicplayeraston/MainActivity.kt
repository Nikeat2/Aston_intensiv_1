package com.example.musicplayeraston

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnPlay: ImageButton = findViewById(R.id.btnPlay)
        val btnToNextSong: ImageButton = findViewById(R.id.btnNextSong)
        val btnToPreviousSong: ImageButton = findViewById(R.id.btnPreviousSong)
        val btnPause: ImageButton = findViewById(R.id.btnPause)

        btnPlay.setOnClickListener {
            playMusic()
        }

        btnPause.setOnClickListener {
            pauseMusic()
        }

        btnToNextSong.setOnClickListener {
            nextTrack()
        }

        btnToPreviousSong.setOnClickListener {
            previousTrack()
        }

        val musicServiceIntent = Intent(this, MusicService::class.java)
        startForegroundService(musicServiceIntent)
    }

    private fun playMusic() {
        val playIntent = Intent(this, MusicService::class.java).apply {
            action = MusicService.ACTION_PLAY
        }
        startForegroundService(playIntent)
        isPlaying = true
    }

    private fun pauseMusic() {
        val pauseIntent = Intent(this, MusicService::class.java).apply {
            action = MusicService.ACTION_PAUSE
        }
        startForegroundService(pauseIntent)
        isPlaying = false
    }

    private fun nextTrack() {
        val nextIntent = Intent(this, MusicService::class.java).apply {
            action = MusicService.ACTION_NEXT
        }
        startForegroundService(nextIntent)
    }

    private fun previousTrack() {
        val previousIntent = Intent(this, MusicService::class.java).apply {
            action = MusicService.ACTION_PREVIOUS
        }
        startForegroundService(previousIntent)
    }
}