package com.example.playlistmaker.domain.settings.api

interface ThemeInteractor {
    fun getCurrentTheme(): Boolean
    fun saveTheme(darkThemeEnabled: Boolean)
}