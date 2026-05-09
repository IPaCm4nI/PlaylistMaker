package com.example.playlistmaker.playlist.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.mediateka.data.db.entity.TrackEntity
import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM my_playlist")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

//    @Query("UPDATE my_playlist SET tracksInPlaylist = :listTracks WHERE id = :id")
//    suspend fun updateListTracks(id: Int, listTracks: String)
//
//    @Query("SELECT tracksInPlaylist FROM my_playlist WHERE id = :id")
//    suspend fun getAllTracksInPlaylist(id: Int): Flow<String>
}