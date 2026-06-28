package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl(private val searchHistoryRepository: SearchHistoryRepository) : SearchHistoryInteractor {
    override fun getHistory(): ArrayList<Track> {
        return searchHistoryRepository.getHistory()
    }

    override fun addTrackToHistory(
        track: Track,
        onHistoryClick: () -> Unit
    ) {
        searchHistoryRepository.addTrackToHistory(track, onHistoryClick)
    }

    override fun clearHistory() {
        searchHistoryRepository.clearHistory()
    }

    override fun fillTracksHistory() {
        searchHistoryRepository.fillTracksHistory()
    }

    override fun getTracks(): ArrayList<Track> {
        return searchHistoryRepository.getTracks()
    }
}