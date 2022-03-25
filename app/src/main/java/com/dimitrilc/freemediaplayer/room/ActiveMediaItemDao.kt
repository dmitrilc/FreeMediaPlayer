package com.dimitrilc.freemediaplayer.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dimitrilc.freemediaplayer.entities.ActiveMediaItem
import com.dimitrilc.freemediaplayer.entities.MediaItem

@Dao
interface ActiveMediaItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(value: ActiveMediaItem)

    @Query("SELECT * FROM active_item")
    suspend fun getOnce(): ActiveMediaItem

    @Query("SELECT * FROM media_items WHERE id = (SELECT mediaItemId FROM active_item)")
    suspend fun getMediaItemOnce(): MediaItem

    @Query("SELECT * FROM media_items WHERE id = (SELECT mediaItemId FROM active_item)")
    fun getMediaItemLiveData(): LiveData<MediaItem>

}