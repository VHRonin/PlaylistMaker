package com.example.playlistmaker.domain.search.api

import com.example.playlistmaker.domain.search.SearchResult

interface TracksRepository {
    fun searchTracks(term: String): SearchResult
}