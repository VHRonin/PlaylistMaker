package com.example.playlistmaker.domain.db.impl

import com.example.playlistmaker.domain.db.api.SavedTracksInteractor
import com.example.playlistmaker.domain.db.api.SavedTracksRepository
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

class SavedTracksInteractorImpl(private val savedTracksRepository: SavedTracksRepository) : SavedTracksInteractor {
    override suspend fun save(track: Track): Boolean {
        track.isFavorite = true
        savedTracksRepository.save(track)

        return track.isFavorite
    }

    override suspend fun delete(track: Track): Boolean {
        track.isFavorite = false
        savedTracksRepository.delete(track)

        return track.isFavorite
    }

    override fun getSavedTracks(): Flow<List<Track>> = savedTracksRepository.getSavedTracks()
    override fun getTracksIds(): Flow<List<String>> = savedTracksRepository.getTracksIds()
}