package com.example.playlistmaker.data.db.impl

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import com.example.playlistmaker.data.db.convertors.PlaylistDbConvertor
import com.example.playlistmaker.data.db.convertors.TrackDbConvertor
import com.example.playlistmaker.data.db.dao.PlaylistDao
import com.example.playlistmaker.data.db.dao.TrackInPlaylistDao
import com.example.playlistmaker.data.db.entities.PlaylistEntity
import com.example.playlistmaker.data.db.entities.TrackInPlaylistEntity
import com.example.playlistmaker.domain.db.api.PlaylistRepository
import com.example.playlistmaker.domain.db.model.Playlist
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.roundToInt

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

        deleteTracksByIds(playlist.tracksIds)
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

    override fun getTracksByIds(ids: List<Long>): Flow<List<Track>> =
        trackInPlaylistDao.getTracksByIds(ids).map { entities ->
            val tracksById = convertTracksInPlaylist(entities)
                .associateBy { it.trackId }

            ids.asReversed().mapNotNull { trackId ->
                tracksById[trackId]
            }
        }

    override fun getTracksDurationsByIds(ids: List<Long>): Flow<String> = trackInPlaylistDao.getTracksDurationsByIds(ids).map { durations ->
        val totalSec = durations.sumOf { t ->
            val p = t.split(":")
            (p.getOrNull(0)?.toLongOrNull() ?: 0L) * 60 + (p.getOrNull(1)?.toLongOrNull() ?: 0L)
        }
        (totalSec / 60.0).roundToInt().toString()

    }

    override suspend fun deleteTrackById(id: Long, playlist: Playlist) = withContext(Dispatchers.IO){
        playlist.tracksIds.remove(id)
        playlist.tracksNumber = playlist.tracksIds.size

        val playlistEntity = playlistDbConvertor.map(playlist)
        val updated = playlistDao.updateTracksIds(playlistEntity.tracksIds, playlist.tracksNumber, playlistEntity.id)

        val playlists = convertPlaylists(playlistDao.getAllPlaylists().first())
        val isInPlaylist = playlists.any { playlist -> id in playlist.tracksIds }

        if (!isInPlaylist){
            trackInPlaylistDao.deleteTrack(id)
        }
    }

    private suspend fun deleteTracksByIds(ids: List<Long>) = withContext(Dispatchers.IO){
        ids.forEach { id ->
            val playlists = convertPlaylists(playlistDao.getAllPlaylists().first())
            val isInPlaylist = playlists.any { playlist -> id in playlist.tracksIds }

            if (!isInPlaylist){
                trackInPlaylistDao.deleteTrack(id)
            }
        }
    }

    override fun getPlaylistById(id: Long): Flow<Playlist> = playlistDao.getPlaylistById(id).map { playlistEntity ->
        playlistDbConvertor.map(playlistEntity)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(
            playlistDbConvertor.map(playlist)
        )
    }


    private fun convertPlaylists(playlists: List<PlaylistEntity>): List<Playlist>{
        return playlists.map { playlistEntity -> playlistDbConvertor.map(playlistEntity) }
    }

    private fun convertTracksInPlaylist(trackInPlaylistEntities: List<TrackInPlaylistEntity>): List<Track>{
        return trackInPlaylistEntities.map { track -> trackConvertor.mapTrackInPlaylist(track) }
    }
}