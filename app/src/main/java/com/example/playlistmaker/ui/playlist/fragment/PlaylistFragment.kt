package com.example.playlistmaker.ui.playlist.fragment

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.domain.db.model.Playlist
import com.example.playlistmaker.ui.create_playlist.fragment.CreatePlaylistFragment
import com.example.playlistmaker.ui.player.NavigationFrom
import com.example.playlistmaker.ui.player.fragment.PlayerFragment
import com.example.playlistmaker.ui.playlist.PlaylistNavArgs
import com.example.playlistmaker.ui.playlist.view_model.PlaylistViewModel
import com.example.playlistmaker.ui.search.TrackAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    companion object {
        private const val ARGS = "args"
        fun createArgs(playlistId: Long): Bundle = bundleOf(ARGS to playlistId)
        private const val OVERLAY_MAX_ALPHA = 0.5f
    }

    private var id: Long? = null

    private val viewModel: PlaylistViewModel by viewModel()
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetCallback: BottomSheetBehavior.BottomSheetCallback

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
        prepareEditBottomSheet()

        trackAdapter = TrackAdapter(
            debounceClick = viewModel::debounceClick,
            navigationFrom = NavigationFrom.PlaylistFragment,
            onLongClick = {id -> showDeleteTrackMessage(id)}
        )
        binding.tracksRecyclerView.adapter = trackAdapter

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
            bindPlaylistBottomSheet(state.playlist)

            trackAdapter.tracks = state.tracks
            trackAdapter.notifyDataSetChanged()

            val hasTracks = state.playlist.tracksNumber > 0
            binding.noTracksFoundError.isVisible = !hasTracks
            binding.tracksRecyclerView.isVisible = hasTracks
        }

        viewModel.observeShowNoTracksToShareSnackBar().observe(viewLifecycleOwner){
            Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun initViewItems(playlist: Playlist){
        val tracksCount = resources.getQuantityString(R.plurals.tracks_Count, playlist.tracksNumber)

        binding.apply {
            Glide
                .with(this@PlaylistFragment)
                .load(playlist.artworkPath)
                .placeholder(R.drawable.ic_placeholder_track)
                .error(R.drawable.ic_placeholder_track)
                .transform(CenterCrop())
                .into(playlistImage)

            desc.isVisible = playlist.playlistDesc.isNotEmpty() && playlist.playlistDesc.isNotBlank()

            name.text = playlist.playlistName
            desc.text = playlist.playlistDesc
            tracksNum.text = "${playlist.tracksNumber} ${tracksCount}"

            shareButton.setOnClickListener {
                setShareClickListener(playlist, tracksCount)
            }

            deleteButtonBottomSheet.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                showDeletePlaylistMessage(playlist.playlistName)
            }

            editButtonBottomSheet.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                findNavController().navigate(R.id.action_playlistFragment_to_createPlaylistFragment,
                    CreatePlaylistFragment.createArgs(playlist)
                )
            }

            shareButtonBottomSheet.setOnClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                setShareClickListener(playlist, tracksCount)
            }
        }
    }

    private fun setShareClickListener(playlist: Playlist, tracksCount: String){
        if (playlist.tracksNumber > 0){
            viewModel.sharePlaylist(tracksCount)
        }
        else {
            viewModel.showNoTracksFound(getString(R.string.no_tracks))
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
            .setMessage(R.string.want_to_delete)
            .setNegativeButton(R.string.NO) { dialog, which ->
            }
            .setPositiveButton(R.string.YES) { dialog, which ->
                viewModel.deleteTrack(id)
            }
            .show()
    }

    private fun showDeletePlaylistMessage(name: String){
        val message = getString(R.string.want_to_delete_playlist, name)
        MaterialAlertDialogBuilder(requireContext(), R.style.LightAlertDialog)
            .setTitle(R.string.delete_playlist)
            .setMessage(message)
            .setNegativeButton(R.string.NO) { dialog, which ->
            }
            .setPositiveButton(R.string.YES) { dialog, which ->
                viewModel.deletePlaylist(onDeleted = {
                    findNavController().popBackStack()
                })
            }
            .show()
    }

    private fun prepareEditBottomSheet(){
        bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

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
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.editPlaylistBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)

        binding.threePointsButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.overlay.setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN){
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, enabled = true){
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN){
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }

            else findNavController().popBackStack()
        }
    }

    fun bindPlaylistBottomSheet(playlist: Playlist){
        val tracksOrTrackText = resources.getQuantityString(R.plurals.tracks_Count, playlist.tracksNumber)
        val tracksNumText = "${playlist.tracksNumber} $tracksOrTrackText"
        val roundedCorners = dpToPx(2f, requireContext())
        Glide
            .with(this)
            .load(playlist.artworkPath)
            .placeholder(R.drawable.ic_placeholder_track)
            .error(R.drawable.ic_placeholder_track)
            .transform()
            .transform(CenterCrop(), RoundedCorners(roundedCorners))
            .into(binding.playlistBottomSheet.playlistImage)

        binding.playlistBottomSheet.name.text = playlist.playlistName
        binding.playlistBottomSheet.tracksNum.text = tracksNumText
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

    override fun onDestroyView() {
        bottomSheetBehavior.removeBottomSheetCallback(bottomSheetCallback)
        super.onDestroyView()
        _binding = null
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }
}