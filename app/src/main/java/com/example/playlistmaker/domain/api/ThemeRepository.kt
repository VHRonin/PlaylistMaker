package com.example.playlistmaker.domain.api

interface ThemeRepository {
    fun getCurrentTheme(): Boolean
    fun saveTheme(darkThemeEnabled: Boolean)
}