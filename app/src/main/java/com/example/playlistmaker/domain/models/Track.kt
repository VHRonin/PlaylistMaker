package com.example.playlistmaker.domain.models

import com.google.gson.annotations.SerializedName

data class Track(
    val trackName: String?,
    val artistName: String?,
    @SerializedName("trackTimeMillis") var trackTime: String?,
    val artworkUrl100: String?,
    val trackId: Long?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String
){

    fun getCoverArtwork() = artworkUrl100?.replaceAfterLast('/',"512x512bb.jpg")
}