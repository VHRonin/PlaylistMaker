package com.example.playlistmaker.ui.library.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentLibraryBinding
import com.example.playlistmaker.ui.create_playlist.fragment.CreatePlaylistFragment
import com.example.playlistmaker.ui.library.LibraryViewPagerAdapter
import com.example.playlistmaker.ui.library.view_model.LibraryViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class LibraryFragment : Fragment() {
    private lateinit var binding: FragmentLibraryBinding
    private lateinit var tabMediator: TabLayoutMediator
    private val viewModel: LibraryViewModel by viewModel<LibraryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.libraryToolBar)

        binding.viewPager.adapter = LibraryViewPagerAdapter(childFragmentManager, lifecycle)


        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager){ tab, position ->
            when (position){
                0 -> tab.text = getString(R.string.saved_tracks)
                1 -> tab.text = getString(R.string.playlists)
            }
        }

        tabMediator.attach()

        parentFragmentManager.setFragmentResultListener(CreatePlaylistFragment.PLAYLIST_CREATED_KEY, viewLifecycleOwner){ _, bundle ->
            val snackBarText = bundle.getString(CreatePlaylistFragment.PLAYLIST_NAME_ARG_KEY) ?: return@setFragmentResultListener

            viewModel.onPlaylistCreatedResult(snackBarText)
        }

        viewModel.observeShowSnackBar().observe(viewLifecycleOwner){
            Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabMediator.detach()
    }

}