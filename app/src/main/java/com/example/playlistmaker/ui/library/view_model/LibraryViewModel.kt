package com.example.playlistmaker.ui.library.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.ui.library.SingleLiveEvent

class LibraryViewModel : ViewModel() {
    private val showSnackBar = SingleLiveEvent<String>()
    fun observeShowSnackBar(): LiveData<String> = showSnackBar

    fun onPlaylistCreatedResult(text: String){
        showSnackBar.value = text
    }
}