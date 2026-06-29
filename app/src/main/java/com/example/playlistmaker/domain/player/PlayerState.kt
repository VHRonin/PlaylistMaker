package com.example.playlistmaker.domain.player

sealed interface PlayerState {
    data object Default : PlayerState
    data object Prepared : PlayerState
    data object Playing : PlayerState
    data object Paused : PlayerState
}