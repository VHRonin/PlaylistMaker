package com.example.playlistmaker

import com.example.playlistmaker.data.MediaPlayerClient
import com.example.playlistmaker.data.PlayerRepositoryImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.player.MediaPlayerClientImpl
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.PlayerRepository
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {
    private fun getRetrofitNetworkClient(): RetrofitNetworkClient{
        return RetrofitNetworkClient()
    }
    private fun getTracksRepositoryImpl(): TracksRepositoryImpl{
        return TracksRepositoryImpl(getRetrofitNetworkClient())
    }
    private fun getMediaPlayerClient(): MediaPlayerClient{
        return MediaPlayerClientImpl()
    }
    private fun getPlayerRepository(): PlayerRepository{
        return PlayerRepositoryImpl(getMediaPlayerClient())
    }
    fun provideTrackInteractor(): TracksInteractorImpl{
        return TracksInteractorImpl(getTracksRepositoryImpl())
    }
    fun providePlayerInteractor(): PlayerInteractor{
        return PlayerInteractorImpl(getPlayerRepository())
    }
}