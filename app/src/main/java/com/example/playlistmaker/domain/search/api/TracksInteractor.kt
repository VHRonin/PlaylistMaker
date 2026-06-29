package com.example.playlistmaker.domain.search.api

import com.example.playlistmaker.domain.search.SearchResult

interface TracksInteractor {
    fun searchTracks(term: String, consumer: TracksConsumer)

    interface TracksConsumer{
        fun consume(searchResult: SearchResult)
    }
}