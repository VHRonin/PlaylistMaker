package com.example.playlistmaker.data.search.dto

class TracksResponse(
    val resultCount: Int,
    val results: List<TrackDto>
) : Response()