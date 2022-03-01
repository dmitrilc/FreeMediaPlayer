package com.example.freemediaplayer.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.freemediaplayer.entities.MediaItem

@Dao
interface MediaItemDao {
    @Query("SELECT * FROM media_items")
    suspend fun getAll(): List<MediaItem>

    @Query("SELECT DISTINCT uri FROM media_items")
    suspend fun getUris(): List<String>

    @Insert(onConflict = REPLACE)
    suspend fun insert(item: MediaItem)

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(item: Collection<MediaItem>)
}