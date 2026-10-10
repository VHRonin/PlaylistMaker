package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.data.db.entities.PlaylistEntity
import com.example.playlistmaker.domain.db.model.Playlist
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlistEntity: PlaylistEntity)

    @Delete
    suspend fun deletePlaylist(playlistEntity: PlaylistEntity)

    @Query("SELECT * FROM playlist_table")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("UPDATE playlist_table SET tracksIds = :tracksIds, tracksNumber = :tracksNum WHERE id = :id")
    fun updateTracksIds(tracksIds: String, tracksNum: Int, id: Long): Int

    @Query("SELECT * FROM playlist_table WHERE id = :id")
    fun getPlaylistById(id: Long): Flow<PlaylistEntity>

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)
}