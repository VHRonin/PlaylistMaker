package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.data.player.MediaPlayerClient
import com.example.playlistmaker.data.player.MediaPlayerClientImpl
import com.example.playlistmaker.data.player.PlayerRepositoryImpl
import com.example.playlistmaker.data.search.history.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.search.network.TracksRepositoryImpl
import com.example.playlistmaker.data.search.history.SearchHistory
import com.example.playlistmaker.data.search.history.SearchHistoryImpl
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.data.settings.ThemeRepositoryImpl
import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.example.playlistmaker.domain.player.api.PlayerInteractor
import com.example.playlistmaker.domain.player.api.PlayerRepository
import com.example.playlistmaker.domain.player.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.search.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.api.SearchHistoryRepository
import com.example.playlistmaker.domain.search.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.domain.search.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.settings.api.ThemeInteractor
import com.example.playlistmaker.domain.settings.api.ThemeRepository
import com.example.playlistmaker.domain.settings.impl.ThemeInteractorImpl
import com.example.playlistmaker.domain.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl

object Creator {
    private const val SEARCH_PREFERENCES = "search_preferences"
    private const val APP_PREFERENCES = "app_preferences"
    private fun getRetrofitNetworkClient(): RetrofitNetworkClient {
        return RetrofitNetworkClient()
    }
    private fun getTracksRepositoryImpl(): TracksRepositoryImpl {
        return TracksRepositoryImpl(getRetrofitNetworkClient())
    }
    private fun getMediaPlayerClient(): MediaPlayerClient {
        return MediaPlayerClientImpl()
    }
    private fun getPlayerRepository(): PlayerRepository {
        return PlayerRepositoryImpl(getMediaPlayerClient())
    }
    private fun gerSharedPreferences(context: Context) = context.getSharedPreferences(SEARCH_PREFERENCES,
        Context.MODE_PRIVATE
    )
    private fun getSearchHistoryImpl(context: Context): SearchHistory {
        return SearchHistoryImpl(gerSharedPreferences(context))
    }
    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(getSearchHistoryImpl(context))
    }
    private fun getThemeRepository(context: Context): ThemeRepository {
        return ThemeRepositoryImpl(
            context.getSharedPreferences(
                APP_PREFERENCES,
                Context.MODE_PRIVATE
            )
        )
    }
    private fun getExternalNavigator(context: Context): ExternalNavigator{
        return ExternalNavigatorImpl(context)
    }
    fun provideTrackInteractor(): TracksInteractorImpl {
        return TracksInteractorImpl(getTracksRepositoryImpl())
    }
    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository())
    }
    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
    }
    fun provideThemeInteractor(context: Context): ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository(context))
    }
    fun provideSharingInteractor(context: Context): SharingInteractor{
        return SharingInteractorImpl(getExternalNavigator(context))
    }
}