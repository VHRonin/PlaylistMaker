package com.example.playlistmaker.network

import com.example.playlistmaker.tracks.Track

class TracksResponse(
    val resultCount: Int,
    val results: List<Track>
)