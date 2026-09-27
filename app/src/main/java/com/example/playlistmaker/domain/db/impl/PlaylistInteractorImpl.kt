package com.example.playlistmaker.domain.db.impl

import com.example.playlistmaker.domain.db.api.PlaylistInteractor
import com.example.playlistmaker.domain.db.api.PlaylistRepository
import com.example.playlistmaker.domain.db.model.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) : PlaylistInteractor {
    override suspend fun save(playlist: Playlist) {
        playlistRepository.insertPlaylist(playlist)
    }

    override suspend fun delete(playlist: Playlist) {
        playlistRepository.deletePlaylist(playlist)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = playlistRepository.getAllPlaylists()
}