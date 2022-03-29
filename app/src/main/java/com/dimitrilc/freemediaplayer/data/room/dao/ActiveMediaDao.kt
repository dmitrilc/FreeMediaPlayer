package com.dimitrilc.freemediaplayer.data.room.dao

import androidx.room.*
import com.dimitrilc.freemediaplayer.data.entities.ActiveMediaItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ActiveMediaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(value: ActiveMediaItem)

    @Query("SELECT * FROM active_item")
    suspend fun getOnce(): ActiveMediaItem

    @Query("SELECT * FROM active_item")
    fun getObservable(): Flow<ActiveMediaItem>

}