package com.example.playlistmaker.data.player

import com.example.playlistmaker.domain.player.PlayerState
import com.example.playlistmaker.domain.player.api.PlayerRepository

class PlayerRepositoryImpl(private val mediaPlayerClient: MediaPlayerClient) : PlayerRepository {
    override fun preparePlayer(previewUrl: String, onCompletion: () -> Unit) {
        mediaPlayerClient.preparePlayer(previewUrl, onCompletion)
    }

    override fun startPlayer(onStart: () -> Unit) {
        mediaPlayerClient.startPlayer(onStart)
    }

    override fun pausePlayer(onPause: () -> Unit) {
        mediaPlayerClient.pausePlayer(onPause)
    }

    override fun releasePlayer() {
        mediaPlayerClient.releasePlayer()
    }

    override fun getCurrentTIme(): String {
        return mediaPlayerClient.getCurrentTIme()
    }

    override fun getPlayerState(): PlayerState {
        return when (mediaPlayerClient.getPlayerState()){
            0 -> PlayerState.Default
            1 -> PlayerState.Prepared
            2 -> PlayerState.Playing
            3 -> PlayerState.Paused
            else -> PlayerState.Default

        }
    }
}