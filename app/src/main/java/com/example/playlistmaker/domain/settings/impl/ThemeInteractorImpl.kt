package com.example.playlistmaker.domain.settings.impl

import com.example.playlistmaker.domain.settings.api.ThemeInteractor
import com.example.playlistmaker.domain.settings.api.ThemeRepository

class ThemeInteractorImpl(private val themeRepository: ThemeRepository) : ThemeInteractor {
    override fun getCurrentTheme(): Boolean {
        return themeRepository.getCurrentTheme()
    }

    override fun saveTheme(darkThemeEnabled: Boolean) {
        themeRepository.saveTheme(darkThemeEnabled)
    }
}