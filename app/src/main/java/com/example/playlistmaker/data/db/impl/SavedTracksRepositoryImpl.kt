package com.example.playlistmaker.data.db.impl

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.TrackDbConvertor
import com.example.playlistmaker.data.db.TrackEntity
import com.example.playlistmaker.domain.db.api.SavedTracksRepository
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SavedTracksRepositoryImpl(private val appDb: AppDatabase, private val trackDbConvertor: TrackDbConvertor) : SavedTracksRepository {
    override suspend fun save(track: Track) {
        val trackToSave = trackDbConvertor.map(track)
        appDb.trackDao().insertTrack(trackToSave)
    }

    override suspend fun delete(track: Track) {
        val trackToDelete = trackDbConvertor.map(track)
        appDb.trackDao().deleteTrack(trackToDelete)
    }

    override fun getSavedTracks(): Flow<List<Track>> = flow {
        val foundTracks = appDb.trackDao().getTracks()
        val convertedTracks = convertTracks(foundTracks)
        emit(convertedTracks.reversed())
    }

    private fun convertTracks(tracks: List<TrackEntity>): List<Track>{
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}