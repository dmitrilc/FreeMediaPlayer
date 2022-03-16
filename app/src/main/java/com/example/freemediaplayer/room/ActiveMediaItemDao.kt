package com.example.freemediaplayer.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.freemediaplayer.entities.ActiveMediaItem
import com.example.freemediaplayer.entities.MediaItem

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

/*    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: PlayerState)

    @Update
    suspend fun update(copy: PlayerState)

    @Query("UPDATE active_media SET progress = :progress")
    suspend fun updateProgress(progress: Long)

    @Query("SELECT * FROM active_media")
    fun getObservable(): LiveData<PlayerState>

    @Query("SELECT * FROM active_media")
    suspend fun getOnce(): PlayerState?*/

}