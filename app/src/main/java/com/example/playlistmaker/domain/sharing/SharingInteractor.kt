package com.example.playlistmaker.domain.sharing

import com.example.playlistmaker.domain.sharing.models.EmailData

interface SharingInteractor {
    fun shareApp(link: String)
    fun textSupport(emailData: EmailData)
    fun openUserAgreement(link: String)
}