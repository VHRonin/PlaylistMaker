package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.api.ThemeRepository

class ThemeInteractorImpl(private val themeRepository: ThemeRepository) : ThemeInteractor {
    override fun getCurrentTheme(): Boolean {
        return themeRepository.getCurrentTheme()
    }

    override fun saveTheme(darkThemeEnabled: Boolean) {
        themeRepository.saveTheme(darkThemeEnabled)
    }
}