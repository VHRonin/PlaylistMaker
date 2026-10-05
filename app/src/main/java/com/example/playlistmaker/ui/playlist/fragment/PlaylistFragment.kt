package com.example.playlistmaker.ui.playlist.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.domain.db.model.Playlist
import com.example.playlistmaker.ui.player.NavigationFrom
import com.example.playlistmaker.ui.player.fragment.PlayerFragment
import com.example.playlistmaker.ui.playlist.PlaylistNavArgs
import com.example.playlistmaker.ui.playlist.view_model.PlaylistViewModel
import com.example.playlistmaker.ui.search.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    companion object {
        private const val ARGS = "args"
        fun createArgs(playlistId: Long): Bundle = bundleOf(ARGS to playlistId)
    }

    private var id: Long? = null

    private val viewModel: PlaylistViewModel by viewModel()
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private lateinit var trackAdapter: TrackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getLong(ARGS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolBar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolBar.setNavigationOnClickListener { findNavController().popBackStack() }

        viewModel.getPlaylistById(id!!)
        showBottomSheet()

        trackAdapter = TrackAdapter(
            debounceClick = viewModel::debounceClick,
            navigationFrom = NavigationFrom.PlaylistFragment,
            onLongClick = {id -> showDeleteTrackMessage(id)}
        )
        binding.tracksRecyclerView.adapter = trackAdapter
//        viewModel.searchTracks()

        viewModel.observeState().observe(viewLifecycleOwner){ state ->
            if (state.tracksDuration.isNotEmpty()){
                val minutesCount = resources.getQuantityString(R.plurals.minutes_count, state.tracksDuration.toInt())
                binding.duration.text = "${state.tracksDuration.toInt()} $minutesCount"
            }
            else {
                val minutesCount = resources.getQuantityString(R.plurals.minutes_count, 0)
                binding.duration.text = "0 $minutesCount"
            }

            initViewItems(state.playlist)

            trackAdapter.tracks = state.tracks
            trackAdapter.notifyDataSetChanged()
        }
    }

    private fun initViewItems(playlist: Playlist){
//        viewModel.getTracksDuration(playlist.tracksIds)
        binding.apply {
            Glide
                .with(this@PlaylistFragment)
                .load(playlist.artworkPath)
                .placeholder(R.drawable.ic_placeholder_track)
                .error(R.drawable.ic_placeholder_track)
                .transform(CenterCrop())
                .into(playlistImage)

            name.text = playlist.playlistName
            desc.text = playlist.playlistDesc
            tracksNum.text = "${playlist.tracksNumber} ${resources.getQuantityString(R.plurals.tracks_Count, playlist.tracksNumber)}"
        }
    }

    private fun showBottomSheet(){
        val behavior = BottomSheetBehavior.from(binding.bottomSheet)
        val gap = resources.getDimensionPixelSize(R.dimen.large_size)

        fun updatePeekHeight() {
            val rootTop = IntArray(2).also { binding.root.getLocationOnScreen(it) }[1]
            val shareTop = IntArray(2).also { binding.shareButton.getLocationOnScreen(it) }[1]
            val shareBottom = shareTop - rootTop + binding.shareButton.height
            behavior.peekHeight = binding.root.height - shareBottom - gap
        }

        binding.shareButton.addOnLayoutChangeListener { _, _, top, _, bottom, _, oldTop, _, oldBottom ->
            if (top != oldTop || bottom != oldBottom) updatePeekHeight()
        }
    }

    private fun showDeleteTrackMessage(id: Long){
        MaterialAlertDialogBuilder(requireContext(), R.style.LightAlertDialog)
            .setTitle(R.string.want_to_delete)
            .setNegativeButton(R.string.NO) { dialog, which ->
            }
            .setPositiveButton(R.string.YES) { dialog, which ->
                viewModel.deleteTrack(id)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}