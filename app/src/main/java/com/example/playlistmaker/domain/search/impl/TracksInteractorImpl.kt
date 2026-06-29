package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.domain.search.api.TracksInteractor
import com.example.playlistmaker.domain.search.api.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {
    private val executors = Executors.newCachedThreadPool()
    override fun searchTracks(
        term: String,
        consumer: TracksInteractor.TracksConsumer
    ) {
        executors.execute {
            consumer.consume(repository.searchTracks(term))
        }
    }
}