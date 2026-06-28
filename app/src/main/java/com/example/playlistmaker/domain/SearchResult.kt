package com.example.playlistmaker.domain

import com.example.playlistmaker.domain.models.Track

sealed class SearchResult {

    data class Success(val foundTracks: List<Track>, val code: Int) : SearchResult()
    data class NetworkError(val code: Int) : SearchResult()
    data class NothingFound(val code: Int) : SearchResult()
}