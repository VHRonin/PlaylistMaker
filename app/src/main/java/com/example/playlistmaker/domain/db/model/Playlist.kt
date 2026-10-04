package com.example.playlistmaker.domain.db.model

import android.os.Parcelable
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    var playlistName: String,
    var playlistDesc: String,
    val artworkPath: String,
    val tracksIds: MutableList<Long>,
    var tracksNumber: Int,
    val id: Long = 0
) : Parcelable
