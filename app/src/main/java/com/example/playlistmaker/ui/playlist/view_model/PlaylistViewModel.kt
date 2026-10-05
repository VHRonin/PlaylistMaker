package com.example.playlistmaker.ui.playlist.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.api.PlaylistInteractor
import com.example.playlistmaker.domain.db.model.Playlist
import com.example.playlistmaker.ui.playlist.PlaylistUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {
    private var state = MutableLiveData<PlaylistUiState>()

    fun observeState(): LiveData<PlaylistUiState> = state

    private var isClickAllowed = true
    private var debounceClickJob: Job? = null

    fun getPlaylistById(id: Long){
        viewModelScope.launch {
            playlistInteractor.getPlaylistById(id).collect { playlist ->
                state.value = state.value?.copy(playlist = playlist) ?: PlaylistUiState("", emptyList(), playlist)

                searchTracks(playlist.tracksIds)
                getTracksDuration(playlist.tracksIds)
            }
        }
    }

    fun debounceClick(): Boolean{
        val current = isClickAllowed
        if (isClickAllowed){
            isClickAllowed = false
            debounceClickJob = viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }

        return current
    }

    private fun searchTracks(ids: List<Long>){
        viewModelScope.launch {
            playlistInteractor.getTracksByIds(ids).collect { tracks ->
                state.value = state.value?.copy(tracks = tracks)
            }
        }
    }

    private fun getTracksDuration(ids: List<Long>){
        viewModelScope.launch {
            val duration = playlistInteractor.getTracksDurationsByIds(ids).collect { duration ->
                state.value = state.value?.copy(tracksDuration = duration)
            }
        }
    }

    fun deleteTrack(id: Long){
        viewModelScope.launch {
            playlistInteractor.deleteTrackById(id, state.value?.playlist!!)
        }
    }

    companion object{
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}