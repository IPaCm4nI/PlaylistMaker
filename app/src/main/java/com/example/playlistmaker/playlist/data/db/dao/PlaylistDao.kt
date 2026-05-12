package com.example.playlistmaker.playlist.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.mediateka.data.db.entity.TrackEntity
import com.example.playlistmaker.playlist.data.db.entity.AddedTracksEntity
import com.example.playlistmaker.playlist.data.db.entity.PlaylistEntity
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM my_playlist")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackInAddedTracks(track: AddedTracksEntity)

    @Update
    suspend fun updatePlaylist(playlistEntity: PlaylistEntity)
}