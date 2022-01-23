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

    @Query("SELECT DISTINCT type FROM audio")
    suspend fun getTypes(): List<String>

//    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<User>
//
//    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): User
//
    @Insert(onConflict = REPLACE)
    suspend fun insert(audio: Audio)

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(audios: List<Audio>)

//    @Delete
//    fun delete(user: User)

}