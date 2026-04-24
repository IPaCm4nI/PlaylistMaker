package com.example.playlistmaker.mediateka.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.mediateka.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Query("DELETE FROM favourite_table WHERE id = :id")
    suspend fun deleteTrack(id: Int)

    @Query("SELECT * FROM favourite_table")
    fun getTracks(): Flow<List<TrackEntity>>

    @Query("SELECT id from favourite_table")
    suspend fun getIdTrack(): List<Int>
}