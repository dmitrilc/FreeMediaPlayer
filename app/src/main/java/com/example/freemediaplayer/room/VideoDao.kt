package com.example.freemediaplayer.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.freemediaplayer.entities.Video

@Dao
interface VideoDao {
    @Query("SELECT * FROM video")
    suspend fun getAll(): List<Video>

    @Query("SELECT DISTINCT uri FROM video")
    suspend fun getUris(): List<String>

    @Insert(onConflict = REPLACE)
    suspend fun insert(video: Video)

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(videos: Collection<Video>)

}