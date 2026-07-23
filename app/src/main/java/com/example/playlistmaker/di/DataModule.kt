package com.example.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import com.example.playlistmaker.data.player.MediaPlayerClient
import com.example.playlistmaker.data.player.MediaPlayerClientImpl
import com.example.playlistmaker.data.search.history.SearchHistory
import com.example.playlistmaker.data.search.history.SearchHistoryImpl
import com.example.playlistmaker.data.search.network.ITunesApi
import com.example.playlistmaker.data.search.network.NetworkClient
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.factory
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single<ITunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }

    factory {
        Gson()
    }

    factory {
        MediaPlayer()
    }

    single(named(SEARCH_PREFERENCES)) {
        androidContext().getSharedPreferences(SEARCH_PREFERENCES,
            Context.MODE_PRIVATE
        )
    }

    factory<MediaPlayerClient>{
        MediaPlayerClientImpl(get())
    }

    single<SearchHistory>{
        SearchHistoryImpl(get(named(SEARCH_PREFERENCES)), get())
    }

    single<NetworkClient>{
        RetrofitNetworkClient(get())
    }
}

private const val SEARCH_PREFERENCES = "search_preferences"