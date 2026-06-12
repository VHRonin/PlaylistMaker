package com.example.playlistmaker.domain

sealed class PlayerState {
    data object Default : PlayerState()
    data object Prepared : PlayerState()
    data object Playing : PlayerState()
    data object Paused : PlayerState()
}