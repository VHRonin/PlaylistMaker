package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.models.Track

sealed class SearchResult {

    data class Success(val foundTracks: List<Track>, val code: Int) : SearchResult()
    data class NetworkError(val code: Int) : SearchResult()
    data class NothingFound(val code: Int) : SearchResult()
}