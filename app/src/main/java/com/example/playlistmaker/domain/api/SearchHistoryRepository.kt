package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryRepository {
    fun getHistory(): ArrayList<Track>
    fun addTrackToHistory(track: Track, onHistoryClick: () -> Unit)
    fun clearHistory()
    fun fillTracksHistory()
    fun getTracks(): ArrayList<Track>
}