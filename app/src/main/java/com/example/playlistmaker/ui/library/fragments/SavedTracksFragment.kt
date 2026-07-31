package com.example.playlistmaker.ui.library.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.library.view_model.SavedTracksViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SavedTracksFragment : Fragment() {

    companion object {
        fun newInstance() = SavedTracksFragment()
    }

    private val viewModel by viewModel<SavedTracksViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_saved_tracks, container, false)
    }
}