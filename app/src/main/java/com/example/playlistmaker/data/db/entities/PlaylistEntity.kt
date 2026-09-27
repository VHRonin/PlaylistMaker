package com.example.playlistmaker.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val playlistName: String,
    val playlistDesc: String,
    val artworkPath: String,
    val tracksIds: String,
    val tracksNumber: Int
)
