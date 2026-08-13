package com.example.playlistmaker.ui.search

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.search.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.ui.player.PlayerNavArgs
import com.example.playlistmaker.ui.player.fragment.PlayerFragment

class TrackAdapter(private val debounceClick: () -> Boolean, private val onAddToHistoryClick: (Track) -> Unit) : RecyclerView.Adapter<TrackViewHolder>() {
    var tracks: List<Track> = ArrayList()
    // var onClick: () -> Unit = {}

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TrackViewHolder,
        position: Int
    ) {
        val track = tracks[position]
        holder.bind(track)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context

            if (debounceClick()){
                val navArgs = PlayerNavArgs(
                    track.getCoverArtwork(),
                    track.trackName,
                    track.artistName,
                    track.trackTime,
                    track.collectionName,
                    track.releaseDate,
                    track.primaryGenreName,
                    track.country,
                    track.previewUrl
                )

                it.findNavController().navigate(R.id.action_searchFragment_to_playerFragment,
                    PlayerFragment.createArgs(navArgs)
                    )

                onAddToHistoryClick(track)
            }
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}