package com.example.playlistmaker.domain.db.api

import com.example.playlistmaker.domain.db.model.Playlist
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun save(playlist: Playlist)
    suspend fun delete(playlist: Playlist)
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun updateTracksIds(playlist: Playlist, track: Track): Int
    fun getTracksByIds(ids: List<Long>): Flow<List<Track>>
    fun getTracksDurationsByIds(ids: List<Long>): Flow<String>
}