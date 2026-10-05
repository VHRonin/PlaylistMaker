package com.example.playlistmaker.domain.db.impl

import com.example.playlistmaker.domain.db.api.PlaylistInteractor
import com.example.playlistmaker.domain.db.api.PlaylistRepository
import com.example.playlistmaker.domain.db.model.Playlist
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) : PlaylistInteractor {
    override suspend fun save(playlist: Playlist) {
        playlist.playlistName = playlist.playlistName.trim()
        playlist.playlistDesc = playlist.playlistDesc.trim()
        playlistRepository.insertPlaylist(playlist)
    }

    override suspend fun delete(playlist: Playlist) {
        playlistRepository.deletePlaylist(playlist)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = playlistRepository.getAllPlaylists()
    override suspend fun updateTracksIds(
        playlist: Playlist,
        track: Track
    ): Int {
        return playlistRepository.updateTracksIds(playlist, track)
    }

    override fun getTracksByIds(ids: List<Long>): Flow<List<Track>> = playlistRepository.getTracksByIds(ids)

    override fun getTracksDurationsByIds(ids: List<Long>): Flow<String> = playlistRepository.getTracksDurationsByIds(ids)

    override suspend fun deleteTrackById(
        id: Long,
        playlist: Playlist
    ) = playlistRepository.deleteTrackById(id, playlist)

    override fun getPlaylistById(id: Long): Flow<Playlist> = playlistRepository.getPlaylistById(id)
}