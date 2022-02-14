package com.example.freemediaplayer.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.freemediaplayer.entities.Audio

@Dao
interface AudioDao {
    @Query("SELECT * FROM audio")
    suspend fun getAll(): List<Audio>

    @Query("SELECT DISTINCT uri FROM audio")
    suspend fun getUris(): List<String>

    @Insert(onConflict = REPLACE)
    suspend fun insert(audio: Audio)

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(audios: Collection<Audio>)
}