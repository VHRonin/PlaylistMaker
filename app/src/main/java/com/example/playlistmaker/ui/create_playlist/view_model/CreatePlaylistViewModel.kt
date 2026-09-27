package com.example.playlistmaker.ui.create_playlist.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.ui.create_playlist.CreatePlaylistUiState

class CreatePlaylistViewModel : ViewModel() {
    private val state = MutableLiveData(
        CreatePlaylistUiState("", "", "")
    )

    fun observeState(): LiveData<CreatePlaylistUiState> = state

    fun onNameInputChanged(name: String){
        state.postValue(state.value?.copy(name = name))
    }

    fun onDescriptionInputChanged(desc: String){
        state.postValue(state.value?.copy(desc = desc))
    }

    fun onArtworkChanged(artwork: String){
        state.postValue(state.value?.copy(artwork = artwork))
    }

    fun hasUnsavedChanges(): Boolean =
        (state.value?.name?.isNotEmpty() == true || state.value?.desc?.isNotEmpty() == true || state.value?.artwork?.isNotEmpty() == true)
}