package com.example.playlistmaker.domain.db.api

import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

interface SavedTracksInteractor {
    suspend fun save(track: Track): Boolean
    suspend fun delete(track: Track): Boolean
    fun getSavedTracks(): Flow<List<Track>>
    fun getTracksIds(): Flow<List<String>>
}