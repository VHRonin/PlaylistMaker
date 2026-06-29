package com.example.playlistmaker.domain.sharing.impl

import com.example.playlistmaker.domain.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.domain.sharing.models.EmailData

class SharingInteractorImpl(private val externalNavigator: ExternalNavigator) : SharingInteractor {
    override fun shareApp(link: String) {
        externalNavigator.shareLink(link)
    }

    override fun openUserAgreement(link: String) {
        externalNavigator.openLink(link)
    }

    override fun textSupport(emailData: EmailData) {
        externalNavigator.openEmail(emailData)
    }
}