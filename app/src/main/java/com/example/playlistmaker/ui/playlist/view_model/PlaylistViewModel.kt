package com.example.playlistmaker.ui.playlist.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.api.PlaylistInteractor
import com.example.playlistmaker.ui.playlist.PlaylistUiState
import kotlinx.coroutines.launch

class PlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {
    private var state = MutableLiveData(
        PlaylistUiState("")
    )

    fun observeState(): LiveData<PlaylistUiState> = state

    fun getTracksDuration(ids: List<Long>){
        viewModelScope.launch {
            val duration = playlistInteractor.getTracksDurationsByIds(ids).collect { duration ->
                state.postValue(state.value?.copy(tracksDuration = duration))
            }
        }
    }
}