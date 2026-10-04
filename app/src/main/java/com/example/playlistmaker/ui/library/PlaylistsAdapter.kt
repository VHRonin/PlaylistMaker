package com.example.playlistmaker.ui.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.db.model.Playlist
import com.example.playlistmaker.ui.playlist.PlaylistNavArgs
import com.example.playlistmaker.ui.playlist.fragment.PlaylistFragment

class PlaylistsAdapter(): RecyclerView.Adapter<PlaylistsViewHolder>() {
    var playlists: List<Playlist> = ArrayList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist, parent, false)
        return PlaylistsViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PlaylistsViewHolder,
        position: Int
    ) {
        val playlist = playlists[position]
        holder.bind(playlist)

        holder.itemView.setOnClickListener {
            it.findNavController().navigate(R.id.action_libraryFragment_to_playlistFragment,
                PlaylistFragment.createArgs(PlaylistNavArgs(playlist)))
        }
    }

    override fun getItemCount(): Int = playlists.size
}