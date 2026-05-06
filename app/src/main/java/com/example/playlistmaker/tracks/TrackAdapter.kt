package com.example.playlistmaker.tracks

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.PlayerActivity
import com.example.playlistmaker.R

class TrackAdapter() : RecyclerView.Adapter<TrackViewHolder>() {
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
        holder.bind(tracks[position])

        holder.itemView.setOnClickListener {
            searchHistory.addTrackToHistory(tracks[position], onHistoryClick = onClick)

            val context = holder.itemView.context

            val playerIntent = Intent(context, PlayerActivity::class.java)

            context.startActivity(playerIntent)
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}