package com.example.playlistmaker.domain.db.api

import com.example.playlistmaker.domain.db.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun insertPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    fun getAllPlaylists(): Flow<List<Playlist>>
}