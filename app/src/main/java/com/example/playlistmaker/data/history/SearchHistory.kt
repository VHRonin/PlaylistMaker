package com.example.playlistmaker.data.history

import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.models.Track

interface SearchHistory {
    fun getHistory(): ArrayList<TrackDto>
    fun addTrackToHistory(track: TrackDto, onHistoryClick: () -> Unit)
    fun clearHistory()
    fun fillTracksHistory()
    fun getTracks(): ArrayList<TrackDto>
}