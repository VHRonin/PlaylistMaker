package com.example.playlistmaker.data

interface MediaPlayerClient {
    fun preparePlayer(previewUrl: String, onCompletion: () -> Unit)
    fun startPlayer(onStart: () -> Unit)
    fun pausePlayer(onPause: () -> Unit)
    fun releasePlayer()
    fun getCurrentTIme(): String
    fun getPlayerState(): Int
}