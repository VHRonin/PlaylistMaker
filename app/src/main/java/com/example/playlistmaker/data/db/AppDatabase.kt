package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.db.dao.PlaylistDao
import com.example.playlistmaker.data.db.dao.TrackDao
import com.example.playlistmaker.data.db.dao.TrackInPlaylistDao
import com.example.playlistmaker.data.db.entities.PlaylistEntity
import com.example.playlistmaker.data.db.entities.TrackEntity
import com.example.playlistmaker.data.db.entities.TrackInPlaylistEntity

@Database(version = 3, entities = [TrackEntity::class, PlaylistEntity::class, TrackInPlaylistEntity::class])
abstract class AppDatabase : RoomDatabase(){
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun trackInPlaylistDao(): TrackInPlaylistDao
}