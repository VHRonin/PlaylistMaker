package com.example.playlistmaker.ui.settings.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.settings.api.ThemeInteractor
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.models.EmailData

class SettingsViewModel(
    private val themeInteractor: ThemeInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {
    companion object{
        fun getFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val themeInteractor = Creator.provideThemeInteractor(context)
                val sharingInteractor = Creator.provideSharingInteractor(context)
                SettingsViewModel(themeInteractor, sharingInteractor)
            }
        }
    }

    private val darkTheme = MutableLiveData<Boolean>(false)
    fun observeTheme(): LiveData<Boolean> = darkTheme

    init {
        darkTheme.value = themeInteractor.getCurrentTheme()
    }

    fun changeTheme(checked: Boolean){
        themeInteractor.saveTheme(checked)
        darkTheme.value = checked
    }

    fun shareApp(link: String) {
        sharingInteractor.shareApp(link)
    }

    fun textSupport(emailData: EmailData) {
        sharingInteractor.textSupport(emailData)
    }

    fun openUserAgreement(link: String) {
        sharingInteractor.openUserAgreement(link)
    }
}