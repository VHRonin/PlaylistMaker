package com.example.playlistmaker.data.db.impl

import com.example.playlistmaker.data.db.convertors.PlaylistDbConvertor
import com.example.playlistmaker.data.db.convertors.TrackDbConvertor
import com.example.playlistmaker.data.db.dao.PlaylistDao
import com.example.playlistmaker.data.db.dao.TrackInPlaylistDao
import com.example.playlistmaker.data.db.entities.PlaylistEntity
import com.example.playlistmaker.domain.db.api.PlaylistRepository
import com.example.playlistmaker.domain.db.model.Playlist
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistDbConvertor: PlaylistDbConvertor,
    private val trackConvertor: TrackDbConvertor,
    private val trackInPlaylistDao: TrackInPlaylistDao
) : PlaylistRepository {
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
        convertPlaylists(playlistEntities).reversed()
    }

    override suspend fun updateTracksIds(
        playlist: Playlist,
        track: Track
    ): Int {
        val updated: Int
        withContext(Dispatchers.IO){
            playlist.tracksIds.add(track.trackId!!)
            playlist.tracksNumber = playlist.tracksIds.size

            val trackEntity = trackConvertor.mapTrackInPlaylist(track)
            val playlistEntity = playlistDbConvertor.map(playlist)

            trackInPlaylistDao.insertTrack(trackEntity)
            updated = playlistDao.updateTracksIds(playlistEntity.tracksIds, playlist.tracksNumber, playlistEntity.id)
        }
        return updated
    }

    private fun convertPlaylists(playlists: List<PlaylistEntity>): List<Playlist>{
        return playlists.map { playlistEntity -> playlistDbConvertor.map(playlistEntity) }
    }
}