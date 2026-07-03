package com.example.playlistmaker.ui.player

import com.example.playlistmaker.domain.player.PlayerState

data class PlayerUiState(
    val playerState: PlayerState,
    val trackTimer: String
)
