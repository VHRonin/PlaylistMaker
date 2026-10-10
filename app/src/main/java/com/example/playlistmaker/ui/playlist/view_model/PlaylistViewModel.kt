package com.example.playlistmaker.ui.playlist.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.api.PlaylistInteractor
import com.example.playlistmaker.domain.db.model.Playlist
import com.example.playlistmaker.domain.sharing.SharingInteractor
import com.example.playlistmaker.ui.SingleLiveEvent
import com.example.playlistmaker.ui.playlist.PlaylistUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlaylistViewModel(private val playlistInteractor: PlaylistInteractor, private val sharingInteractor: SharingInteractor) : ViewModel() {
    private var state = MutableLiveData<PlaylistUiState>()

    fun observeState(): LiveData<PlaylistUiState> = state

    private var isClickAllowed = true
    private var debounceClickJob: Job? = null
    private var playlistJob: Job? = null
    private var tracksJob: Job? = null
    private var durationJob: Job? = null

    private val showNoTracksToShareSnackBar = SingleLiveEvent<String>()
    fun observeShowNoTracksToShareSnackBar(): LiveData<String> = showNoTracksToShareSnackBar

    fun getPlaylistById(id: Long){
        playlistJob?.cancel()
        playlistJob = viewModelScope.launch {
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
        tracksJob?.cancel()
        tracksJob = viewModelScope.launch {
            playlistInteractor.getTracksByIds(ids).collect { tracks ->
                state.value = state.value?.copy(tracks = tracks)
            }
        }
    }

    private fun getTracksDuration(ids: List<Long>){
        durationJob?.cancel()
        durationJob = viewModelScope.launch {
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

    fun sharePlaylist(textNum: String){
        var textToShare = "${state.value?.playlist?.playlistName}\n${state.value?.playlist?.playlistDesc}\n${state.value?.tracks?.size} $textNum\n"
        val tracks = state.value?.tracks

        tracks?.forEachIndexed { index, track ->
            val text = "${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTime})\n"
            textToShare += text
        }

        sharingInteractor.shareApp(textToShare)
    }

    fun deletePlaylist(onDeleted: () -> Unit){
        val playlist = state.value?.playlist ?: return
        playlistJob?.cancel()

        viewModelScope.launch {
            playlistInteractor.delete(playlist)
            onDeleted()
        }
    }

    fun showNoTracksFound(text: String){
        showNoTracksToShareSnackBar.value = text
    }

    companion object{
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}