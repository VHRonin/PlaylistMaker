package com.example.playlistmaker.ui.player

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayerNavArgs(
    val artwork: String?,
    val trackName: String?,
    val artistName: String?,
    val trackTime: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?) : Parcelable
