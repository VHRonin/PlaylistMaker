package com.example.playlistmaker.domain.settings.api

interface ThemeRepository {
    fun getCurrentTheme(): Boolean
    fun saveTheme(darkThemeEnabled: Boolean)
}