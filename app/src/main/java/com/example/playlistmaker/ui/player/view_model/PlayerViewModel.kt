package com.example.playlistmaker.ui.player.view_model

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.domain.player.PlayerState
import com.example.playlistmaker.domain.player.api.PlayerInteractor
import com.example.playlistmaker.ui.player.PlayerUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(private val playerInteractor: PlayerInteractor) : ViewModel() {

    companion object{
        const val TRACK_TIME_DELAY = 300L
    }

    private val playerUiState = MutableLiveData<PlayerUiState>(
        PlayerUiState(
            playerState = PlayerState.Default,
            trackTimer = "00:00"
        )
    )
    fun observeState(): LiveData<PlayerUiState> = playerUiState
    private var timerJob: Job? = null

    fun releasePlayer() {
        playerInteractor.releasePlayer()
        timerJob?.cancel()
    }

    fun pausePlayer(){
        playerInteractor.pausePlayer {
            playerUiState.postValue(
                playerUiState.value?.copy(playerState = PlayerState.Paused)
            )
        }
        timerJob?.cancel()
    }

    fun handlePlayButton(){
        when (playerInteractor.getPlayerState()){
            is PlayerState.Playing -> pausePlayer()
            is PlayerState.Paused, is PlayerState.Prepared -> startPlayer()
            is PlayerState.Default -> {}
        }
    }

    fun preparePlayer(previewUrl: String){
        playerInteractor.preparePlayer(previewUrl){
            playerUiState.postValue(
                PlayerUiState(playerState = PlayerState.Prepared, trackTimer = "00:00")
            )
        }
        playerUiState.postValue(
            playerUiState.value?.copy(playerState = PlayerState.Prepared)
        )
    }

    private fun startPlayer(){
        playerInteractor.startPlayer {
            playerUiState.postValue(
                playerUiState.value?.copy(playerState = PlayerState.Playing)
            )
        }

        timerJob = viewModelScope.launch {
            run()
        }
    }

    private suspend fun run() {
        while (playerInteractor.getPlayerState() is PlayerState.Playing){
            delay(TRACK_TIME_DELAY)
            if (playerInteractor.getPlayerState() is PlayerState.Playing){
                playerUiState.postValue(
                    playerUiState.value?.copy(trackTimer = playerInteractor.getCurrentTIme())
                )
            }
        }
    }
}
