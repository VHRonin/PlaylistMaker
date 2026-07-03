package com.example.playlistmaker.ui.search.view_model

import com.example.playlistmaker.domain.search.SearchResult

sealed interface SearchState {
    data object Default : SearchState
    data object Loading : SearchState
    data class Result(val searchResult: SearchResult) : SearchState
}