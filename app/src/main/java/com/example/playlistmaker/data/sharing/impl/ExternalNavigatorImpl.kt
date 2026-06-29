package com.example.playlistmaker.data.sharing.impl

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.models.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {
    override fun shareLink(link: String) {

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, link)

        context.startActivity(shareIntent)
    }

    override fun openLink(link: String) {
        val agreementIntent = Intent(Intent.ACTION_VIEW)
        agreementIntent.data = link.toUri()

        context.startActivity(agreementIntent)
    }

    override fun openEmail(emailData: EmailData) {
        val supportIntent = Intent(Intent.ACTION_SENDTO)

        supportIntent.data = "mailto:".toUri()
        supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.email))
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
        supportIntent.putExtra(Intent.EXTRA_TEXT, emailData.text)

        context.startActivity(supportIntent)
    }
}