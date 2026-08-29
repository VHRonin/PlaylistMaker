package com.example.playlistmaker.ui.search.view_model

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
import com.example.playlistmaker.domain.search.SearchResult
import com.example.playlistmaker.domain.search.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.search.api.TracksInteractor
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {
    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val TEXT = ""
        const val CLICK_DEBOUNCE_DELAY = 1000L
        const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val searchUiState = MutableLiveData<SearchUiState>(
        SearchUiState(
            tracks = arrayListOf(),
            searchState = SearchState.Default,
            historyTracks = arrayListOf()
        )
    )

    init {
        getTrackHistory()
    }
    fun observeState(): LiveData<SearchUiState> = searchUiState
    private var searchString: String = TEXT
    private var lastFailedTerm = ""
    private var isClickAllowed = true
    private var lastSearchedTerm: String? = null
    private var debounceClickJob: Job? = null
    private var seaarchDebounceJob: Job? = null

    fun debounceClick(): Boolean{
        val current = isClickAllowed
        if (isClickAllowed){
            isClickAllowed = false
//            handler.postDelayed({isClickAllowed = true}, CLICK_DEBOUNCE_DELAY)
            debounceClickJob = viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }

        return current
    }

    fun addTextToQuery(text: String){
        searchString = text
    }

    fun searchDebounce(){
//        handler.removeCallbacks(searchRunnable)
//        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)

        seaarchDebounceJob?.cancel()
        seaarchDebounceJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            findTracks(searchString)
        }
    }

    fun clearTracks(){
        searchUiState.postValue(
            searchUiState.value?.copy().apply {
                this?.tracks?.clear()
            }
        )
    }

    fun retrySearch(){
        findTracks(lastFailedTerm)
    }

    fun clearSearch(){
        clearTracks()
        searchUiState.postValue(
            searchUiState.value?.copy().apply {
                this?.searchState = SearchState.Default
            }
        )
    }

    fun addTrackToHistory(track: Track){
        searchHistoryInteractor.addTrackToHistory(track, onHistoryClick = {getTrackHistory()})
    }

    fun getTrackHistory(){
        searchUiState.postValue(
            searchUiState.value?.copy().apply {
                this?.historyTracks?.clear()
                this?.historyTracks?.addAll(searchHistoryInteractor.getHistory())
            }
        )
    }

    fun clearHistory(){
        searchHistoryInteractor.clearHistory()
    }

    override fun onCleared() {
        super.onCleared()
//        handler.removeCallbacks(searchRunnable)
        seaarchDebounceJob?.cancel()
    }

    private fun findTracks(text: String){
        if (text.isNotEmpty()){
            if (text == lastSearchedTerm && searchUiState.value?.searchState is SearchState.Result){
                return
            }

            clearTracks()
            searchUiState.postValue(searchUiState.value?.copy().apply { this?.searchState = SearchState.Loading })

            viewModelScope.launch {
                tracksInteractor.searchTracks(text).collect { searchResult ->
                    processNetworkResult(searchResult, text)
                }

            }
        }
    }

    private fun processNetworkResult(searchResult: SearchResult, text: String){
        when (searchResult){
            is SearchResult.Success -> {
                lastSearchedTerm = text
                searchUiState.postValue(
                    searchUiState.value?.copy().apply {
                        this?.tracks?.clear()
                        this?.tracks?.addAll(searchResult.foundTracks)
                        this?.searchState = SearchState.Result(
                            SearchResult.Success(searchResult.foundTracks, searchResult.code)
                        )
                    }
                )
            }
            is SearchResult.NothingFound -> {
                lastSearchedTerm = text
                searchUiState.postValue(
                    searchUiState.value?.copy().apply {
                        clearTracks()
                        this?.searchState = SearchState.Result(
                            SearchResult.NothingFound(searchResult.code)
                        )
                    }
                )
            }
            else -> {
                searchUiState.postValue(
                    searchUiState.value?.copy().apply {
                        clearTracks()
                        this?.searchState = SearchState.Result(
                            SearchResult.NetworkError((searchResult as SearchResult.NetworkError).code)
                        )
                    }
                )
                lastFailedTerm = searchString
            }
        }
    }
}