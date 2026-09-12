package com.example.playlistmaker.domain.db.impl

import com.example.playlistmaker.domain.db.api.SavedTracksInteractor
import com.example.playlistmaker.domain.db.api.SavedTracksRepository
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

class SavedTracksInteractorImpl(private val savedTracksRepository: SavedTracksRepository) : SavedTracksInteractor {
    override suspend fun save(track: Track): Boolean {
        track.isFavorite = !track.isFavorite
        savedTracksRepository.save(track)

        return track.isFavorite
    }

    override suspend fun delete(track: Track) {
        savedTracksRepository.delete(track)
    }

    override fun getSavedTracks(): Flow<List<Track>> = savedTracksRepository.getSavedTracks()
}