package com.example.freemediaplayer.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.freemediaplayer.entities.Video

@Dao
interface VideoDao {
    @Query("SELECT * FROM Video")
    suspend fun getAll(): List<Video>

    @Query("SELECT DISTINCT uri FROM Video")
    suspend fun getUris(): List<String>

    @Query("SELECT DISTINCT path FROM video")
    suspend fun getPaths(): List<String>

    @Insert(onConflict = REPLACE)
    suspend fun insert(Video: Video)

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(Videos: List<Video>)

}