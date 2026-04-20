package com.example.playlistmaker.tracks

import com.google.gson.annotations.SerializedName

data class Track(
    val trackName: String?,
    val artistName: String?,
    @SerializedName("trackTimeMillis") var trackTime: String?,
    val artworkUrl100: String?,
    val trackId: Long?
)