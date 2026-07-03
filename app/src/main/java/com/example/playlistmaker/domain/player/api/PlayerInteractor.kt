package com.example.playlistmaker.domain.player.api

import com.example.playlistmaker.domain.player.PlayerState

interface PlayerInteractor {
    fun preparePlayer(previewUrl: String, onCompletion: () -> Unit)
    fun startPlayer(onStart: () -> Unit)
    fun pausePlayer(onPause: () -> Unit)
    fun releasePlayer()
    fun getCurrentTIme(): String
    fun getPlayerState(): PlayerState
}