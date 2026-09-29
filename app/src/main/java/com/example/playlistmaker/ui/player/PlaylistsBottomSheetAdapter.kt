package com.example.playlistmaker.ui.player

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.db.model.Playlist
import com.example.playlistmaker.ui.library.PlaylistsViewHolder

class PlaylistsBottomSheetAdapter: RecyclerView.Adapter<PlaylistsBottomSheetViewHolder>() {
    var playlists: List<Playlist> = ArrayList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistsBottomSheetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_bottom_sheet, parent, false)
        return PlaylistsBottomSheetViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PlaylistsBottomSheetViewHolder,
        position: Int
    ) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int = playlists.size
}