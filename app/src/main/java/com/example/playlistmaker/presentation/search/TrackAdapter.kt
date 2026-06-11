package com.example.playlistmaker.presentation.search

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.tracks.SearchHistory
import com.example.playlistmaker.presentation.player.PlayerActivity

class TrackAdapter(private val debounceClick: () -> Boolean) : RecyclerView.Adapter<TrackViewHolder>() {
    var tracks: List<Track> = ArrayList()
    lateinit var searchHistory: SearchHistory
    var onClick: () -> Unit = {}

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
                val playerIntent = Intent(context, PlayerActivity::class.java)
                playerIntent.putExtra("artwork", track.getCoverArtwork())
                playerIntent.putExtra("trackName", track.trackName)
                playerIntent.putExtra("artistName", track.artistName)
                playerIntent.putExtra("trackTime", track.trackTime)
                playerIntent.putExtra("collectionName", track.collectionName)
                playerIntent.putExtra("releaseDate", track.releaseDate)
                playerIntent.putExtra("primaryGenreName", track.primaryGenreName)
                playerIntent.putExtra("country", track.country)
                playerIntent.putExtra("previewUrl", track.previewUrl)

                context.startActivity(playerIntent)

                searchHistory.addTrackToHistory(track, onHistoryClick = onClick)
            }
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}