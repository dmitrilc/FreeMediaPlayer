package com.dimitrilc.freemediaplayer.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem

@Dao
interface GlobalPlaylistDao {

    @Query("SELECT * FROM global_playlist")
    fun getAllObservable(): LiveData<List<GlobalPlaylistItem>?>

    @Query("SELECT * FROM global_playlist")
    suspend fun getAllOnce(): List<GlobalPlaylistItem>?

    @Transaction
    fun replacePlaylist(playlist: List<GlobalPlaylistItem>){
        deleteAll()
        insertPlayListItems(playlist)
    }

    @Delete
    fun remove(globalPlaylistItem: GlobalPlaylistItem)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPlayListItems(playlist: List<GlobalPlaylistItem>)

    @Query("DELETE FROM global_playlist")
    fun deleteAll()

    @Query("SELECT COUNT(mId) FROM global_playlist")
    suspend fun count(): Long?

}