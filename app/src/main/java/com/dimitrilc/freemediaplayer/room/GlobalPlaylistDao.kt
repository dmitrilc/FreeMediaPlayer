package com.dimitrilc.freemediaplayer.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dimitrilc.freemediaplayer.entities.GlobalPlaylistItem
import com.dimitrilc.freemediaplayer.entities.MediaItem

@Dao
interface GlobalPlaylistDao {

    @Query("SELECT * FROM media_items JOIN global_playlist ON media_items.id=global_playlist.mediaItemId ORDER BY global_playlist.mId")
    fun getGlobalPlaylist(): LiveData<List<MediaItem>>

    @Query("SELECT * FROM media_items JOIN global_playlist ON media_items.id=global_playlist.mediaItemId ORDER BY global_playlist.mId")
    suspend fun getOnce(): List<MediaItem>

    @Transaction
    suspend fun replacePlaylist(playlist: List<GlobalPlaylistItem>){
        deleteAll()
        insertPlayListItems(playlist)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlayListItems(playlist: List<GlobalPlaylistItem>)

    @Query("DELETE FROM global_playlist")
    fun deleteAll()

}