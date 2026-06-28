package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
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