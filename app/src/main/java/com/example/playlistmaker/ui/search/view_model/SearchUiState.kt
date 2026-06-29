package com.example.playlistmaker.ui.search.view_model

import com.example.playlistmaker.domain.search.models.Track

data class SearchUiState(
    val tracks: ArrayList<Track>,
    var searchState: SearchState,
    val historyTracks: ArrayList<Track>
)
