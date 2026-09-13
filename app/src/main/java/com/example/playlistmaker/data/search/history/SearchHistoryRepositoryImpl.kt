package com.example.playlistmaker.data.search.history

import android.icu.text.SimpleDateFormat
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.search.dto.TrackDto
import com.example.playlistmaker.domain.search.api.SearchHistoryRepository
import com.example.playlistmaker.domain.search.models.Track
import java.util.Locale

class SearchHistoryRepositoryImpl(private val searchHistory: SearchHistory, private val appDb: AppDatabase) :
    SearchHistoryRepository {
    override suspend fun getHistory(): ArrayList<Track> {
        val tracks = searchHistory.getHistory()

        return tracks.map {
            formatTrackFromDto(it)
        } as ArrayList<Track>
    }

    override fun addTrackToHistory(
        track: Track,
        onHistoryClick: () -> Unit
    ) {
        searchHistory.addTrackToHistory(formatTrackToDto(track), onHistoryClick)
    }

    override fun clearHistory() {
        searchHistory.clearHistory()
    }

    override suspend fun getTracks(): ArrayList<Track> {
        return searchHistory.getTracks().map {
            formatTrackFromDto(it)
        } as ArrayList<Track>
    }

    private fun formatTrackToDto(track: Track): TrackDto {
        return TrackDto(
            track.trackName,
            track.artistName,
            timeToMillis(track.trackTime!!),
            track.artworkUrl100,
            track.trackId,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.isFavorite
        )
    }

    private suspend fun formatTrackFromDto(track: TrackDto): Track {
        val savedTracksIds = appDb.trackDao().getTracksIds()
        return Track(
            track.trackName,
            track.artistName,
            formatTime(track.trackTime),
            track.artworkUrl100,
            track.trackId,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.trackId.toString() in savedTracksIds
        )
    }

    private fun timeToMillis(time: String): Long {
        val (minutes, seconds) = time.split(":").map { it.toLong() }
        return (minutes * 60 + seconds) * 1000
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