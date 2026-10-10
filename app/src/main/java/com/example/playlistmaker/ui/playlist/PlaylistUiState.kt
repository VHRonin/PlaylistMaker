package com.example.playlistmaker.ui.playlist

import com.example.playlistmaker.domain.db.model.Playlist
import com.example.playlistmaker.domain.search.models.Track

data class PlaylistUiState(
    val tracksDuration: String,
    val tracks: List<Track>,
    val playlist: Playlist
)
