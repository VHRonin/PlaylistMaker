package com.example.playlistmaker.ui.settings.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.settings.api.ThemeInteractor
import com.example.playlistmaker.domain.sharing.models.EmailData
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val shareButton = findViewById<MaterialTextView>(R.id.share)
        val supportButton = findViewById<MaterialTextView>(R.id.support)
        val userAgreementButton = findViewById<MaterialTextView>(R.id.user_agreement)
        val settingsToolBar = findViewById<MaterialToolbar>(R.id.settingsToolBar)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        val viewModel: SettingsViewModel = ViewModelProvider(
            this,
            SettingsViewModel.getFactory(this)
        ).get(SettingsViewModel::class.java)

        setSupportActionBar(settingsToolBar)
        settingsToolBar.setNavigationOnClickListener { finish() }

        shareButton.setOnClickListener { viewModel.shareApp(getString(R.string.course_link)) }
        supportButton.setOnClickListener {
            viewModel.textSupport(
                EmailData(
                    getString(R.string.email),
                    getString(R.string.mail_theme),
                    getString(R.string.mail_body)
                )
            )
        }
        userAgreementButton.setOnClickListener { viewModel.openUserAgreement(getString(R.string.agreement_link)) }

        viewModel.observeTheme().observe(this){
            if (themeSwitcher.isChecked != it){
                themeSwitcher.isChecked = it
            }
            AppCompatDelegate.setDefaultNightMode(
                if (it){
                    AppCompatDelegate.MODE_NIGHT_YES
                }
                else{
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.changeTheme(checked)
        }
    }
}