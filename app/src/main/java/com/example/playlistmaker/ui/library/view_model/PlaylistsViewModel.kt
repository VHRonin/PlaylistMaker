package com.example.playlistmaker.ui.library.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.api.PlaylistInteractor
import com.example.playlistmaker.ui.library.PlaylistsUiState
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {
    private val state = MutableLiveData<PlaylistsUiState>(PlaylistsUiState.Loading)
    fun observeState(): LiveData<PlaylistsUiState> = state

    fun searchPlaylists(){
        viewModelScope.launch {
            playlistInteractor.getAllPlaylists().collect { playlists ->
                if (playlists.isNotEmpty()) state.postValue(PlaylistsUiState.Content(playlists))
                else state.postValue(PlaylistsUiState.Error)
            }
        }
    }
}