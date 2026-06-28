package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.SearchResult
import com.example.playlistmaker.domain.models.Track

interface TracksInteractor {
    fun searchTracks(term: String, consumer: TracksConsumer)

    interface TracksConsumer{
        fun consume(searchResult: SearchResult)
    }
}