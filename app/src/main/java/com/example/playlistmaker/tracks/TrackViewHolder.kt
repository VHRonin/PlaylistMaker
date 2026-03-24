package com.example.playlistmaker.tracks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val trackName = itemView.findViewById<TextView>(R.id.trackName)
    val artistName = itemView.findViewById<TextView>(R.id.artistName)
    val trackTime = itemView.findViewById<TextView>(R.id.trackTime)
    val artworkUrl100 = itemView.findViewById<ImageView>(R.id.artworkUrl100)

    fun bind(item: Track){
        trackName.text = item.trackName
        artistName.text = item.artistName
        trackTime.text = item.trackTime

        Glide
            .with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder_track)
            .error(R.drawable.ic_placeholder_track)
            .centerCrop()
            .transform(RoundedCorners(2))
            .into(artworkUrl100)
    }
}