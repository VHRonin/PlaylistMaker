package com.example.playlistmaker.data.player

import android.media.MediaPlayer
import com.example.playlistmaker.data.player.MediaPlayerClient
import java.text.SimpleDateFormat
import java.util.Locale

class MediaPlayerClientImpl(private val mediaPlayer: MediaPlayer) : MediaPlayerClient {
    private var playerState = MEDIA_DEFAULT

    override fun preparePlayer(previewUrl: String, onCompletion: () -> Unit) {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = MEDIA_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            onCompletion()
            playerState = MEDIA_PREPARED
        }
    }

    override fun startPlayer(onStart: () -> Unit) {
        if (playerState == MEDIA_PREPARED || playerState == MEDIA_PAUSED) {
            mediaPlayer.start()
            playerState = MEDIA_PLAYING
        }
        onStart()
    }

    override fun pausePlayer(onPause: () -> Unit) {
        if (playerState == MEDIA_PLAYING) {
            mediaPlayer.pause()
            playerState = MEDIA_PAUSED
        }
        onPause()
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun getCurrentTIme(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }

    override fun getPlayerState(): Int {
        return playerState
    }

    companion object{
        const val MEDIA_DEFAULT = 0
        const val MEDIA_PREPARED = 1
        const val MEDIA_PLAYING = 2
        const val MEDIA_PAUSED = 3
    }
}