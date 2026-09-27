package com.example.playlistmaker.data.db.impl

import com.example.playlistmaker.data.db.convertors.PlaylistDbConvertor
import com.example.playlistmaker.data.db.dao.PlaylistDao
import com.example.playlistmaker.data.db.entities.PlaylistEntity
import com.example.playlistmaker.domain.db.api.PlaylistRepository
import com.example.playlistmaker.domain.db.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(private val playlistDao: PlaylistDao, private val playlistDbConvertor: PlaylistDbConvertor) : PlaylistRepository {
    override suspend fun insertPlaylist(playlist: Playlist) {
        playlistDao.insertPlaylist(
            playlistDbConvertor.map(playlist)
        )
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistDao.deletePlaylist(
            playlistDbConvertor.map(playlist)
        )
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = playlistDao.getAllPlaylists().map { playlistEntities ->
        convertPlaylists(playlistEntities)
    }

    private fun convertPlaylists(playlists: List<PlaylistEntity>): List<Playlist>{
        return playlists.map { playlistEntity -> playlistDbConvertor.map(playlistEntity) }
    }
}