package com.example.playlistmaker.data.search.history

import com.example.playlistmaker.data.search.dto.TrackDto

interface SearchHistory {
    fun getHistory(): ArrayList<TrackDto>
    fun addTrackToHistory(track: TrackDto, onHistoryClick: () -> Unit)
    fun clearHistory()
//    fun fillTracksHistory()
    fun getTracks(): ArrayList<TrackDto>
}