package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.settings.api.ThemeInteractor

private const val APP_PREFERENCES = "app_preferences"
private const val DARK_THEME_KEY = "dark_theme"
class App : Application() {
    var darkTheme = false
    // lateinit var sharedPrefs: SharedPreferences
    lateinit var themeInteractor: ThemeInteractor

    override fun onCreate() {
        super.onCreate()

        themeInteractor = Creator.provideThemeInteractor(this)

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