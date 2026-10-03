package com.example.playlistmaker.ui.create_playlist

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreatePlaylistResult(
    val playlistName: String,
    val isSuccess: Boolean
) : Parcelable