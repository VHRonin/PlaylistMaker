package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.PlayerState

interface PlayerRepository {
    fun preparePlayer(previewUrl: String, onCompletion: () -> Unit)
    fun startPlayer(onStart: () -> Unit)
    fun pausePlayer(onPause: () -> Unit)
    fun releasePlayer()
    fun getCurrentTIme(): String
    fun getPlayerState(): PlayerState
}