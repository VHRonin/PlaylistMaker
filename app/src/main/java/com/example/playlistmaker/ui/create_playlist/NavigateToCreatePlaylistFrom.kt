package com.example.playlistmaker.ui.create_playlist

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed interface NavigateToCreatePlaylistFrom : Parcelable {
    data object LibraryFragment: NavigateToCreatePlaylistFrom
    data object PlayerFragment: NavigateToCreatePlaylistFrom
}