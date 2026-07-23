package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.sharingModule
import com.example.playlistmaker.di.viewModelModule
import com.example.playlistmaker.domain.settings.api.ThemeInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

private const val APP_PREFERENCES = "app_preferences"
private const val DARK_THEME_KEY = "dark_theme"
class App : Application(), KoinComponent {
    var darkTheme = false
    // lateinit var sharedPrefs: SharedPreferences
    private val themeInteractor: ThemeInteractor by inject()

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                dataModule,
                interactorModule,
                repositoryModule,
                viewModelModule,
                sharingModule
            )
        }

        darkTheme = themeInteractor.getCurrentTheme()

        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean){
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled){
                AppCompatDelegate.MODE_NIGHT_YES
            }
            else{
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}