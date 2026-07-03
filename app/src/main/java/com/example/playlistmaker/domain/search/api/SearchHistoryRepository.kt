package com.example.playlistmaker.domain.search.api

import com.example.playlistmaker.domain.search.models.Track

interface SearchHistoryRepository {
    fun getHistory(): ArrayList<Track>
    fun addTrackToHistory(track: Track, onHistoryClick: () -> Unit)
    fun clearHistory()
    fun fillTracksHistory()
    fun getTracks(): ArrayList<Track>
}