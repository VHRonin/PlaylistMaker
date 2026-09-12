package com.example.playlistmaker.data.search.network

import android.icu.text.SimpleDateFormat
import com.example.playlistmaker.data.search.dto.TracksRequest
import com.example.playlistmaker.data.search.dto.TracksResponse
import com.example.playlistmaker.domain.search.SearchResult
import com.example.playlistmaker.domain.search.api.TracksRepository
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Locale

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(term: String): Flow<SearchResult> = flow {
        val response = networkClient.doRequest(TracksRequest(term))

        if (response.resultCode == 200){
            if ((response as TracksResponse).results.isNotEmpty()){
                emit(SearchResult.Success(
                    response.results.map {
                        Track(
                            it.trackName,
                            it.artistName,
                            formatTime(it.trackTime),
                            it.artworkUrl100,
                            it.trackId,
                            it.collectionName,
                            it.releaseDate,
                            it.primaryGenreName,
                            it.country,
                            it.previewUrl
                        )
                    }, response.resultCode
                ))
            }
            else {
                emit(SearchResult.NothingFound(response.resultCode))
            }
        }
        else {
            emit(SearchResult.NetworkError(response.resultCode))
        }
    }

    fun formatTime(trackTime: Long?): String{
        val millis = trackTime ?: return "--:--"
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        )
            .format(millis)
            .toString()
    }
}