package com.example.playlistmaker.ui.player.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.domain.player.PlayerState
import com.example.playlistmaker.ui.player.PlayerNavArgs
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.search.dpToPx
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class PlayerFragment : Fragment() {
    private lateinit var binding: FragmentPlayerBinding
    private lateinit var args: PlayerNavArgs
    private lateinit var previewUrl: String
    private val viewModel by viewModel<PlayerViewModel>()

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
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.playerToolBar)

        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.playerToolBar.setNavigationOnClickListener { findNavController().popBackStack() }

        initValues()
        checkValues()

        viewModel.observeState().observe(viewLifecycleOwner){
            when(it.playerState){
                is PlayerState.Paused, is PlayerState.Prepared -> binding.playerButton.setImageResource(R.drawable.ic_play_button)
                is PlayerState.Playing, is PlayerState.Default -> binding.playerButton.setImageResource(R.drawable.ic_stop_button)
            }

            binding.trackCurrentTime.text = it.trackTimer
        }

        viewModel.preparePlayer(previewUrl)

        binding.playerButton.setOnClickListener {
            viewModel.handlePlayButton()
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

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.releasePlayer()
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
    }
}