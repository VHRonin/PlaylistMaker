package com.example.playlistmaker.domain.api

interface ThemeInteractor {
    fun getCurrentTheme(): Boolean
    fun saveTheme(darkThemeEnabled: Boolean)
}