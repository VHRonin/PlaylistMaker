package com.example.playlistmaker.domain.db.api

import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

interface SavedTracksRepository {
    suspend fun save(track: Track)
    suspend fun delete(track: Track)
    fun getSavedTracks(): Flow<List<Track>>
    fun getTracksIds(): Flow<List<String>>
}