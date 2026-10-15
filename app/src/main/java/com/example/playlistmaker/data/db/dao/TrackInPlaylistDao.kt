package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entities.TrackInPlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackInPlaylistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTrack(trackInPlaylistEntity: TrackInPlaylistEntity)

    @Query("SELECT * FROM track_in_playlist_table WHERE id IN (:ids)")
    fun getTracksByIds(ids: List<Long>): Flow<List<TrackInPlaylistEntity>>

    @Query("SELECT trackTime FROM track_in_playlist_table WHERE id IN (:ids)")
    fun getTracksDurationsByIds(ids: List<Long>): Flow<List<String>>

    @Query("DELETE FROM track_in_playlist_table WHERE id = :id")
    suspend fun deleteTrack(id: Long)
}