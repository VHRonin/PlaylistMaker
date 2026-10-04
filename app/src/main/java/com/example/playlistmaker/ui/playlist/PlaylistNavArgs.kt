package com.example.playlistmaker.ui.playlist

import android.os.Parcelable
import com.example.playlistmaker.domain.db.model.Playlist
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaylistNavArgs(val playlist: Playlist) : Parcelable
