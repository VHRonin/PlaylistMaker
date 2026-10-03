package com.example.playlistmaker.ui.create_playlist.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.SQLiteException
import com.example.playlistmaker.domain.db.api.PlaylistInteractor
import com.example.playlistmaker.domain.db.model.Playlist
import com.example.playlistmaker.ui.create_playlist.CreatePlaylistResult
import com.example.playlistmaker.ui.create_playlist.CreatePlaylistUiState
import com.example.playlistmaker.ui.SingleLiveEvent
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {
    private val state = MutableLiveData(
        CreatePlaylistUiState("", "", "")
    )

    fun observeState(): LiveData<CreatePlaylistUiState> = state

    private val finishCreation = SingleLiveEvent<CreatePlaylistResult>()
    fun observeFInishCreation(): LiveData<CreatePlaylistResult> = finishCreation

    fun onNameInputChanged(name: String){
        state.postValue(state.value?.copy(name = name))
    }

    fun onDescriptionInputChanged(desc: String){
        state.postValue(state.value?.copy(desc = desc))
    }

    fun onArtworkChanged(artwork: String){
        state.postValue(state.value?.copy(artwork = artwork))
    }

    fun onCreateClicked(){
        val playlist = Playlist(
            state.value!!.name,
            state.value!!.desc,
            state.value!!.artwork,
            mutableListOf(),
            0
        )


        viewModelScope.launch {
            playlistInteractor.save(playlist)
            finishCreation.value = CreatePlaylistResult(playlist.playlistName, true)
        }
    }

    fun hasUnsavedChanges(): Boolean =
        (state.value?.name?.isNotEmpty() == true || state.value?.desc?.isNotEmpty() == true || state.value?.artwork?.isNotEmpty() == true)
}