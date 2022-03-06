package com.example.freemediaplayer.room

import androidx.room.*
import com.example.freemediaplayer.entities.MediaItem

@Dao
interface GlobalPlaylistDao {

    @Transaction
    suspend fun replacePlaylist(playlist: List<MediaItem>){
        deleteAll()
        insertPlayListItems(playlist)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlayListItems(playlist: List<MediaItem>)

    @Query("DELETE FROM global_playlist")
    suspend fun deleteAll()

}