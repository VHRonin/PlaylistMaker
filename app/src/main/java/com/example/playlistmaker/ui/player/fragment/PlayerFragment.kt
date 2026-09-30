package com.example.playlistmaker.ui.player.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.domain.player.PlayerState
import com.example.playlistmaker.ui.create_playlist.NavigateToCreatePlaylistFrom
import com.example.playlistmaker.ui.create_playlist.fragment.CreatePlaylistFragment
import com.example.playlistmaker.ui.player.PlayerNavArgs
import com.example.playlistmaker.ui.player.PlaylistsBottomSheetAdapter
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.search.dpToPx
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class PlayerFragment : Fragment() {
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var args: PlayerNavArgs
    private lateinit var previewUrl: String
    private val viewModel by viewModel<PlayerViewModel>()
    private lateinit var playlistsAdapter: PlaylistsBottomSheetAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            args = it.getParcelable(ARGS)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.playerToolBar)

        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.playerToolBar.setNavigationOnClickListener { findNavController().popBackStack() }

        initValues()
        checkValues()

        binding.likeButton.setOnClickListener {
            viewModel.onSaveClicked(args)
        }

        viewModel.observeState().observe(viewLifecycleOwner){
            when(it.playerState){
                is PlayerState.Paused, is PlayerState.Prepared -> binding.playerButton.setImageResource(R.drawable.ic_play_button)
                is PlayerState.Playing, is PlayerState.Default -> binding.playerButton.setImageResource(R.drawable.ic_stop_button)
            }

            binding.trackCurrentTime.text = it.trackTimer

            binding.likeButton.setImageResource(if (it.isFavorite) R.drawable.ic_like_button_active else R.drawable.ic_like_button)
        }

        viewModel.preparePlayer(previewUrl, args)

        binding.playerButton.setOnClickListener {
            viewModel.handlePlayButton()
        }

        prepareBottomSheet()
        viewModel.observePlaylists().observe(viewLifecycleOwner){
            playlistsAdapter.playlists = it
            playlistsAdapter.notifyDataSetChanged()
        }

        binding.createPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_createPlaylistFragment,
                CreatePlaylistFragment.createArgs(NavigateToCreatePlaylistFrom.PlayerFragment))
        }

        parentFragmentManager.setFragmentResultListener(PLAYLIST_CREATED_KEY, viewLifecycleOwner){ _, bundle ->
            val snackBarText = bundle.getString(PLAYLIST_NAME_ARG_KEY) ?: return@setFragmentResultListener

            viewModel.onPlaylistCreatedResult(snackBarText)
        }

        viewModel.observeCreatedPlaylistShowSnackBar().observe(viewLifecycleOwner){
            Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
        }

        viewModel.observeShowPlaylistUpdated().observe(viewLifecycleOwner){ result ->
            val messageRes = if (result.isSuccess) {
                R.string.playlist_updated
            } else {
                R.string.playlist_update_error
            }
            val message = getString(messageRes, result.name)

            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN){
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }

            Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, enabled = true){
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN){
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }

            else findNavController().popBackStack()
        }
    }

    private fun initValues(){

        val roundedCorners = dpToPx(8f, requireContext())

        Glide
            .with(this)
            .load(args.artwork)
            .placeholder(R.drawable.ic_placeholder_track)
            .error(R.drawable.ic_placeholder_track)
            .centerCrop()
            .transform(RoundedCorners(roundedCorners))
            .into(binding.artwork)

        binding.apply {
            trackName.text = args.trackName
            artistName.text = args.artistName
            trackTimeValue.text = args.trackTime ?: "--:--"
            collectionNameValue.text = args.collectionName
            releaseDateValue.text = args.releaseDate?.take(4)
            primaryGenreNameValue.text = args.primaryGenreName
            countryValue.text = args.country
            previewUrl = args.previewUrl ?: ""
        }
    }

    private fun checkValues(){
        binding.apply {
            if (collectionNameValue.text.isEmpty()) collectionNameGroup.visibility = View.GONE
            if (releaseDateValue.text.isEmpty()) releaseDateGroup.visibility = View.GONE
        }

    }

    private fun prepareBottomSheet(){
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = OVERLAY_MAX_ALPHA * (slideOffset + 1f).coerceIn(0f, 1f)
            }
        })

        binding.addToLibraryButton.setOnClickListener {
            viewModel.searchPlaylists()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        playlistsAdapter = PlaylistsBottomSheetAdapter(
            onSaveToPlaylistClick = { playlist ->
                viewModel.onSaveToPlaylistsClicked(args, playlist)
            }
        )
        binding.playlists.adapter = playlistsAdapter
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN){
            binding.overlay.visibility = View.VISIBLE
            binding.overlay.alpha = OVERLAY_MAX_ALPHA
        }
    }

    companion object {
        private const val ARGS = "args"

        @JvmStatic
        fun newInstance(navArgs: PlayerNavArgs) =
            PlayerFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARGS, args)
                }
            }

        fun createArgs(args: PlayerNavArgs): Bundle =
            bundleOf(ARGS to args)

        const val PLAYLIST_CREATED_KEY = "playlist_created_key"
        const val PLAYLIST_NAME_ARG_KEY = "playlist_name_key"
        private const val OVERLAY_MAX_ALPHA = 0.5f
    }
}