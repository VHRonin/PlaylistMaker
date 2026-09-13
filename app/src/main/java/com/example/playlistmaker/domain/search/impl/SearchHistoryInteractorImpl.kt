package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.domain.search.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.api.SearchHistoryRepository

class SearchHistoryInteractorImpl(private val searchHistoryRepository: SearchHistoryRepository) :
    SearchHistoryInteractor {
    override suspend fun getHistory(): ArrayList<Track> {
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

    override suspend fun getTracks(): ArrayList<Track> {
        return searchHistoryRepository.getTracks()
    }
}