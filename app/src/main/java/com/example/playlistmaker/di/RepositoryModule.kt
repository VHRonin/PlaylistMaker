package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.data.db.impl.SavedTracksRepositoryImpl
import com.example.playlistmaker.data.player.PlayerRepositoryImpl
import com.example.playlistmaker.data.search.history.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.search.network.TracksRepositoryImpl
import com.example.playlistmaker.data.settings.ThemeRepositoryImpl
import com.example.playlistmaker.domain.db.api.SavedTracksRepository
import com.example.playlistmaker.domain.player.api.PlayerRepository
import com.example.playlistmaker.domain.search.api.SearchHistoryRepository
import com.example.playlistmaker.domain.search.api.TracksRepository
import com.example.playlistmaker.domain.settings.api.ThemeRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    single(named(APP_PREFERENCES)){
        androidContext().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
    }
    factory<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

    single<SearchHistoryRepository>{
        SearchHistoryRepositoryImpl(get(), get())
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get(), get())
    }

    single<ThemeRepository> {
        ThemeRepositoryImpl(get(named(APP_PREFERENCES)))
    }

    single<SavedTracksRepository>{
        SavedTracksRepositoryImpl(get(), get())
    }
}

private const val APP_PREFERENCES = "app_preferences"