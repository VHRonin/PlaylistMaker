package com.example.playlistmaker.data

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.ThemeRepository

class ThemeRepositoryImpl(private val sharedPrefs: SharedPreferences) : ThemeRepository {
    override fun getCurrentTheme(): Boolean {
        return sharedPrefs.getBoolean(DARK_THEME_KEY, false)
    }

    override fun saveTheme(darkThemeEnabled: Boolean) {
        sharedPrefs.edit()
            .putBoolean(DARK_THEME_KEY, darkThemeEnabled)
            .apply()
    }

    companion object{
        private const val DARK_THEME_KEY = "dark_theme"
    }
}