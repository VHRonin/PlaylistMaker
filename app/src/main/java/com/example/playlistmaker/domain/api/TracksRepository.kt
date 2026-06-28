package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.SearchResult
import com.example.playlistmaker.domain.models.Track

interface TracksRepository {
    fun searchTracks(term: String): SearchResult
}