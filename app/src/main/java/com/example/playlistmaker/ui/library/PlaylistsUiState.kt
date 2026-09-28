package com.example.playlistmaker.ui.library

import com.example.playlistmaker.domain.db.model.Playlist

sealed interface PlaylistsUiState {
    data object Loading: PlaylistsUiState
    data class Content(val playlists: List<Playlist>): PlaylistsUiState
    data object Error: PlaylistsUiState
}