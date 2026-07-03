package com.example.playlistmaker.domain.player.impl

import com.example.playlistmaker.domain.player.PlayerState
import com.example.playlistmaker.domain.player.api.PlayerInteractor
import com.example.playlistmaker.domain.player.api.PlayerRepository

class PlayerInteractorImpl(private val playerRepository: PlayerRepository) : PlayerInteractor {
    override fun preparePlayer(previewUrl: String, onCompletion: () -> Unit) {
        playerRepository.preparePlayer(previewUrl, onCompletion)
    }

    override fun startPlayer(onStart: () -> Unit) {
        playerRepository.startPlayer(onStart)
    }

    override fun pausePlayer(onPause: () -> Unit) {
        playerRepository.pausePlayer(onPause)
    }

    override fun releasePlayer() {
        playerRepository.releasePlayer()
    }

    override fun getCurrentTIme(): String {
        return playerRepository.getCurrentTIme()
    }

    override fun getPlayerState(): PlayerState {
        return playerRepository.getPlayerState()
    }
}