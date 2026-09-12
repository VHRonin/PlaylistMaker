package com.example.playlistmaker.data.db

import com.example.playlistmaker.domain.search.models.Track

class TrackDbConvertor {
    fun map(track: Track): TrackEntity{
        return TrackEntity(
            track.trackId.toString(),
            track.trackName,
            track.artistName,
            track.trackTime,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )
    }

    fun map(track: TrackEntity): Track{
        return Track(
            track.trackName,
            track.artistName,
            track.trackTime,
            track.artworkUrl100,
            track.id.toLong(),
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl!!
        )
    }
}