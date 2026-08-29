package com.example.playlistmaker.domain.search.api

import com.example.playlistmaker.domain.search.SearchResult
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTracks(term: String): Flow<SearchResult>
}