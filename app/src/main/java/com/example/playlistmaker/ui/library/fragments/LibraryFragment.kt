package com.example.playlistmaker.ui.library.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentLibraryBinding
import com.example.playlistmaker.ui.library.LibraryViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class LibraryFragment : Fragment() {
    private lateinit var binding: FragmentLibraryBinding
    private lateinit var tabMediator: TabLayoutMediator

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabMediator.detach()
    }

}