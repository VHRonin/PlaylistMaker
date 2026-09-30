package com.example.playlistmaker.domain.db.model

import com.example.playlistmaker.domain.search.models.Track

data class Playlist(
    var playlistName: String,
    var playlistDesc: String,
    val artworkPath: String,
    val tracksIds: MutableList<Long>,
    var tracksNumber: Int,
    val id: Long = 0
)
