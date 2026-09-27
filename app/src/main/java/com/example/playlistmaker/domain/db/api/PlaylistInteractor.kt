package com.example.playlistmaker.domain.db.api

import com.example.playlistmaker.domain.db.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun save(playlist: Playlist)
    suspend fun delete(playlist: Playlist)
    fun getAllPlaylists(): Flow<List<Playlist>>
}