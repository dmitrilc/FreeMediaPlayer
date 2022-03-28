package com.dimitrilc.freemediaplayer.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dimitrilc.freemediaplayer.data.entities.ActiveMediaItem
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ActiveMediaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(value: ActiveMediaItem)

    @Query("SELECT * FROM active_item")
    suspend fun getOnce(): ActiveMediaItem

    @Query("SELECT * FROM active_item")
    fun getObservable(): Flow<ActiveMediaItem>

/*    @Query("SELECT * FROM media_items WHERE id = (SELECT mediaItemId FROM active_item)")
    suspend fun getActiveMediaItemOnce(): MediaItem

    @Query("SELECT * FROM media_items WHERE id = (SELECT mediaItemId FROM active_item)")
    fun getMediaItemObservable(): LiveData<MediaItem>*/

}