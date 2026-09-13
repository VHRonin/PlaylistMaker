package com.example.playlistmaker.ui.library.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.databinding.FragmentSavedTracksBinding
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.ui.library.SavedTracksUiState
import com.example.playlistmaker.ui.library.view_model.SavedTracksViewModel
import com.example.playlistmaker.ui.player.NavigationFrom
import com.example.playlistmaker.ui.search.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class SavedTracksFragment : Fragment() {

    companion object {
        fun newInstance() = SavedTracksFragment()
    }

    private val viewModel by viewModel<SavedTracksViewModel>()
    private lateinit var binding: FragmentSavedTracksBinding
    private lateinit var tracksAdapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavedTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeState().observe(viewLifecycleOwner){
            when (it){
                is SavedTracksUiState.Error -> {
                    binding.noTracksFoundError.visibility = View.VISIBLE
                    tracksAdapter.tracks = emptyList()
                    tracksAdapter.notifyDataSetChanged()
                }
                is SavedTracksUiState.Loading -> {binding.noTracksFoundError.visibility = View.GONE}
                is SavedTracksUiState.Content -> {showContent(it.tracks)}
            }
        }

        tracksAdapter = TrackAdapter(
            debounceClick = { viewModel.debounceClick() },
            onAddToHistoryClick = {track ->
                viewModel.addTrackToHistory(track)
            },
            navigationFrom = NavigationFrom.LibraryFragment
        )

        binding.savedTracksRecyclerView.adapter = tracksAdapter
    }

    private fun showContent(tracks: List<Track>){
        binding.noTracksFoundError.visibility = View.GONE
        tracksAdapter.tracks = tracks
        tracksAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        viewModel.searchSavedTracks()
    }
}