package com.example.playlistmaker.ui.library.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.api.SavedTracksInteractor
import com.example.playlistmaker.domain.search.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.models.Track
import com.example.playlistmaker.ui.library.SavedTracksUiState
import com.example.playlistmaker.ui.search.view_model.SearchViewModel.Companion.CLICK_DEBOUNCE_DELAY
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SavedTracksViewModel(
    private val savedTracksInteractor: SavedTracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    init {
        searchSavedTracks()
    }
    private val savedTracksUiState = MutableLiveData<SavedTracksUiState>(
        SavedTracksUiState.Loading
    )
    fun observeState(): LiveData<SavedTracksUiState> = savedTracksUiState

    private var isClickAllowed = true
    private var debounceClickJob: Job? = null

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

    fun addTrackToHistory(track: Track){
        searchHistoryInteractor.addTrackToHistory(track, onHistoryClick = {})
    }

    fun searchSavedTracks(){
        viewModelScope.launch {
            savedTracksInteractor.getSavedTracks().collect { tracks ->
                if (tracks.isNotEmpty()) savedTracksUiState.postValue(SavedTracksUiState.Content(tracks))
                else savedTracksUiState.postValue(SavedTracksUiState.Error)
            }
        }
    }
}