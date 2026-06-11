package com.example.playlistmaker.presentation.search

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import java.util.Locale

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val trackName = itemView.findViewById<TextView>(R.id.trackName)
    val artistName = itemView.findViewById<TextView>(R.id.artistName)
    val trackTime = itemView.findViewById<TextView>(R.id.trackTime)
    val artworkUrl100 = itemView.findViewById<ImageView>(R.id.artworkUrl100)

    fun bind(item: Track){
        trackName.text = item.trackName ?: itemView.context.getString(R.string.unknown_track_name)
        artistName.text = item.artistName ?: itemView.context.getString(R.string.unknown_artist_name)
        trackTime.text = item.trackTime?.let { formatTime(it) } ?: "--:--"

        val roundedCorners = dpToPx(2f, itemView.context)

        Glide
            .with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.ic_placeholder_track)
            .error(R.drawable.ic_placeholder_track)
            .centerCrop()
            .transform(RoundedCorners(roundedCorners))
            .into(artworkUrl100)
    }
}

fun formatTime(trackTime: String?): String{
    val millis = trackTime?.toLongOrNull() ?: return "--:--"
    return SimpleDateFormat(
        "mm:ss",
        Locale.getDefault()
    )
        .format(millis)
        .toString()
}

fun dpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics).toInt()
}