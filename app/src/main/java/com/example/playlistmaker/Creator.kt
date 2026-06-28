package com.example.playlistmaker

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.example.playlistmaker.data.MediaPlayerClient
import com.example.playlistmaker.data.PlayerRepositoryImpl
import com.example.playlistmaker.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.ThemeRepositoryImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.history.SearchHistory
import com.example.playlistmaker.data.history.SearchHistoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.player.MediaPlayerClientImpl
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.PlayerRepository
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.api.ThemeRepository
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.impl.ThemeInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {
    private const val SEARCH_PREFERENCES = "search_preferences"
    private const val APP_PREFERENCES = "app_preferences"
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
    private fun gerSharedPreferences(context: Context) = context.getSharedPreferences(SEARCH_PREFERENCES, MODE_PRIVATE)
    private fun getSearchHistoryImpl(context: Context): SearchHistory{
        return SearchHistoryImpl(gerSharedPreferences(context))
    }
    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository{
        return SearchHistoryRepositoryImpl(getSearchHistoryImpl(context))
    }
    private fun getThemeRepository(context: Context): ThemeRepository{
        return ThemeRepositoryImpl( context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE))
    }
    fun provideTrackInteractor(): TracksInteractorImpl{
        return TracksInteractorImpl(getTracksRepositoryImpl())
    }
    fun providePlayerInteractor(): PlayerInteractor{
        return PlayerInteractorImpl(getPlayerRepository())
    }
    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor{
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
    }
    fun provideThemeInteractor(context: Context): ThemeInteractor{
        return ThemeInteractorImpl(getThemeRepository(context))
    }
}