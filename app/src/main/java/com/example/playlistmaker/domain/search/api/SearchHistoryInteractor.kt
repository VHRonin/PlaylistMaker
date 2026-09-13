package com.example.playlistmaker.domain.search.api

import com.example.playlistmaker.domain.search.models.Track

interface SearchHistoryInteractor {
    suspend fun getHistory(): ArrayList<Track>
    fun addTrackToHistory(track: Track, onHistoryClick: () -> Unit)
    fun clearHistory()
    suspend fun getTracks(): ArrayList<Track>
}