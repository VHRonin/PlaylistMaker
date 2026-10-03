package com.example.playlistmaker.ui.library.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.ui.library.view_model.PlaylistsViewModel
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.domain.db.model.Playlist
import com.example.playlistmaker.ui.create_playlist.NavigateToCreatePlaylistFrom
import com.example.playlistmaker.ui.create_playlist.fragment.CreatePlaylistFragment
import com.example.playlistmaker.ui.library.PlaylistsAdapter
import com.example.playlistmaker.ui.library.PlaylistsUiState
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    companion object {
        fun newInstance() = PlaylistsFragment()
    }

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<PlaylistsViewModel>()
    private lateinit var playlistsAdapter: PlaylistsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_createPlaylistFragment,
                CreatePlaylistFragment.createArgs(NavigateToCreatePlaylistFrom.LibraryFragment))
        }

//        binding.playlistsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        viewModel.searchPlaylists()

        playlistsAdapter = PlaylistsAdapter()
        binding.playlistsRecyclerView.adapter = playlistsAdapter

        viewModel.observeState().observe(viewLifecycleOwner){
            when (it){
                is PlaylistsUiState.Loading -> {
                    binding.noTracksFoundError.visibility = View.GONE
                    binding.playlistsRecyclerView.visibility = View.GONE
                }
                is PlaylistsUiState.Error -> {
                    binding.noTracksFoundError.visibility = View.VISIBLE
                    binding.playlistsRecyclerView.visibility = View.GONE
                }
                is PlaylistsUiState.Content -> {
                    showPlaylists(it.playlists)
                }
            }
        }
    }

    private fun showPlaylists(playlists: List<Playlist>){
        binding.noTracksFoundError.visibility = View.GONE
        binding.playlistsRecyclerView.visibility = View.VISIBLE

        playlistsAdapter.playlists = playlists
        playlistsAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}