package com.example.playlistmaker

import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {
    private fun getRetrofitNetworkClient(): RetrofitNetworkClient{
        return RetrofitNetworkClient()
    }

    private fun getTracksRepositoryImpl(): TracksRepositoryImpl{
        return TracksRepositoryImpl(getRetrofitNetworkClient())
    }

    fun provideTrackInteractor(): TracksInteractorImpl{
        return TracksInteractorImpl(getTracksRepositoryImpl())
    }
}