package com.example.playlistmaker.ui.settings.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.domain.sharing.models.EmailData
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel by viewModel<SettingsViewModel>()

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.settingsToolBar)

        binding.apply {
            share.setOnClickListener { viewModel.shareApp(getString(R.string.course_link)) }
            support.setOnClickListener {
                viewModel.textSupport(
                    EmailData(
                        getString(R.string.email),
                        getString(R.string.mail_theme),
                        getString(R.string.mail_body)
                    )
                )
            }
            userAgreement.setOnClickListener { viewModel.openUserAgreement(getString(R.string.agreement_link)) }
        }

        viewModel.observeTheme().observe(viewLifecycleOwner){
            if (binding.themeSwitcher.isChecked != it){
               binding.themeSwitcher.isChecked = it
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

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.changeTheme(checked)
        }
    }
}