package com.example.playlistmaker.data.db.impl

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.TrackDbConvertor
import com.example.playlistmaker.data.db.TrackEntity
import com.example.playlistmaker.data.db.dao.TrackDao
import com.example.playlistmaker.domain.db.api.SavedTracksRepository
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class SavedTracksRepositoryImpl(private val trackDao: TrackDao, private val trackDbConvertor: TrackDbConvertor) : SavedTracksRepository {
    override suspend fun save(track: Track) {
        val trackToSave = trackDbConvertor.map(track)
        trackDao.insertTrack(trackToSave)
    }

    override suspend fun delete(track: Track) {
        val trackToDelete = trackDbConvertor.map(track)
        trackDao.deleteTrack(trackToDelete)
    }

    override fun getSavedTracks(): Flow<List<Track>> = trackDao.getTracks().map { foundTracks ->
        convertTracks(foundTracks).reversed()
    }

    override fun getTracksIds(): Flow<List<String>> = flow {
        emit(trackDao.getTracksIds())
    }

    private fun convertTracks(tracks: List<TrackEntity>): List<Track>{
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}