package com.example.playlistmaker.domain.db.model

import com.example.playlistmaker.domain.search.models.Track

data class Playlist(
    val playlistName: String,
    val playlistDesc: String,
    val artworkPath: String,
    val tracksIds: List<Track>,
    val tracksNumber: Int
)
