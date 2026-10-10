package com.example.playlistmaker.ui.library

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.db.model.Playlist

class PlaylistsViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val playlistImage = itemView.findViewById<ImageView>(R.id.playlistImage)
    private val playlistName = itemView.findViewById<TextView>(R.id.name)
    private val tracksNum = itemView.findViewById<TextView>(R.id.tracksNum)

    fun bind(playlist: Playlist){
        val tracksOrTrackText = itemView.resources.getQuantityString(R.plurals.tracks_Count, playlist.tracksNumber)
        val tracksNumText = "${playlist.tracksNumber} $tracksOrTrackText"
        val roundedCorners = dpToPx(8f, itemView.context)
        Glide
            .with(itemView)
            .load(playlist.artworkPath)
            .placeholder(R.drawable.ic_placeholder_track)
            .error(R.drawable.ic_placeholder_track)
            .transform()
            .transform(CenterCrop(), RoundedCorners(roundedCorners))
            .into(playlistImage)

        playlistName.text = playlist.playlistName
        tracksNum.text = tracksNumText
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}