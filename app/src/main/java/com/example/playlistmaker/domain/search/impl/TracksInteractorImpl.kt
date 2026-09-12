package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.search.SearchResult
import com.example.playlistmaker.domain.search.api.TracksInteractor
import com.example.playlistmaker.domain.search.api.TracksRepository
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {
    override fun searchTracks(
        term: String,
    ): Flow<SearchResult> {
        return repository.searchTracks(term)
    }
}