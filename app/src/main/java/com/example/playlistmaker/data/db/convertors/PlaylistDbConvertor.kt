package com.example.playlistmaker.data.db.convertors

import com.example.playlistmaker.data.db.entities.PlaylistEntity
import com.example.playlistmaker.domain.db.model.Playlist
import com.example.playlistmaker.domain.search.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConvertor(private val gson: Gson) {
    fun map(playlist: Playlist): PlaylistEntity =
        PlaylistEntity(
            id = playlist.id,
            playlistName = playlist.playlistName,
            playlistDesc = playlist.playlistDesc,
            artworkPath = playlist.artworkPath,
            tracksIds = gson.toJson(playlist.tracksIds),
            tracksNumber = playlist.tracksNumber
        )

    fun map(playlist: PlaylistEntity): Playlist =
        Playlist(
            playlist.playlistName,
            playlist.playlistDesc,
            playlist.artworkPath,
            gson.fromJson(playlist.tracksIds, object : TypeToken<List<Long>>() {}.type),
            playlist.tracksNumber,
            playlist.id
        )
}