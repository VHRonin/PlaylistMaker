package com.example.playlistmaker.ui.library

import com.example.playlistmaker.domain.search.models.Track

sealed interface SavedTracksUiState {
    data object Loading: SavedTracksUiState
    data class Content(val tracks: List<Track>) : SavedTracksUiState
    data object Error: SavedTracksUiState
}