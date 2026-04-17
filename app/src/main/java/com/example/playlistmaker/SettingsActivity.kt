package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.net.toUri
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val shareButton = findViewById<MaterialTextView>(R.id.share)
        val supportButton = findViewById<MaterialTextView>(R.id.support)
        val userAgreementButton = findViewById<MaterialTextView>(R.id.user_agreement)
        val settingsToolBar = findViewById<MaterialToolbar>(R.id.settingsToolBar)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        setSupportActionBar(settingsToolBar)

        settingsToolBar.setNavigationOnClickListener { finish() }

        shareButton.setOnClickListener { shareApp() }

        supportButton.setOnClickListener { textSupport() }

        userAgreementButton.setOnClickListener { openUserAgreement() }

        themeSwitcher.isChecked = (applicationContext as App).darkTheme

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }
    }

    private fun shareApp(){
        val linkToCourse = getString(R.string.course_link)

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = getString(R.string.share_intent_type)
        shareIntent.putExtra(Intent.EXTRA_TEXT, linkToCourse)

        startActivity(shareIntent)
    }

    private fun textSupport(){
        val supportIntent = Intent(Intent.ACTION_SENDTO)

        supportIntent.data = getString(R.string.support_intent_data).toUri()
        supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email)))
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_theme))
        supportIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_body))

        startActivity(supportIntent)
    }

    private fun openUserAgreement(){
        val agreementIntent = Intent(Intent.ACTION_VIEW)
        agreementIntent.data = getString(R.string.agreement_link).toUri()

        startActivity(agreementIntent)
    }
}