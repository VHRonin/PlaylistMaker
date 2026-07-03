package com.example.playlistmaker.di

import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.example.playlistmaker.domain.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharingModule = module {
    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(get())
    }
}