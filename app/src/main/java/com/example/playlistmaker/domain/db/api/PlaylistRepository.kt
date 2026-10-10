package com.example.playlistmaker.domain.db.api

import com.example.playlistmaker.data.db.entities.TrackInPlaylistEntity
import com.example.playlistmaker.domain.db.model.Playlist
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun insertPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun updateTracksIds(playlist: Playlist, track: Track): Int
    fun getTracksByIds(ids: List<Long>): Flow<List<Track>>
    fun getTracksDurationsByIds(ids: List<Long>): Flow<String>
    suspend fun deleteTrackById(id: Long, playlist: Playlist)
    fun getPlaylistById(id: Long): Flow<Playlist>
    suspend fun updatePlaylist(playlist: Playlist)
}