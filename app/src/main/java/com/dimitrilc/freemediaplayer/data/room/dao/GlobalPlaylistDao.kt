package com.dimitrilc.freemediaplayer.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem
import com.dimitrilc.freemediaplayer.data.entities.MediaItem

@Dao
interface GlobalPlaylistDao {

    @Query("SELECT * FROM media_items JOIN global_playlist ON media_items.id=global_playlist.mediaItemId ORDER BY global_playlist.mId")
    fun getGlobalPlaylist(): LiveData<List<MediaItem>>

    @Query("SELECT * FROM media_items JOIN global_playlist ON media_items.id=global_playlist.mediaItemId ORDER BY global_playlist.mId")
    suspend fun getOnce(): List<MediaItem>

    @Transaction
    fun replacePlaylist(playlist: List<GlobalPlaylistItem>){
        deleteAll()
        insertPlayListItems(playlist)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPlayListItems(playlist: List<GlobalPlaylistItem>)

    @Query("DELETE FROM global_playlist")
    fun deleteAll()

}