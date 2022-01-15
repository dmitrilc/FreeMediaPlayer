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

//    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
//    fun loadAllByIds(userIds: IntArray): List<User>
//
//    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
//            "last_name LIKE :last LIMIT 1")
//    fun findByName(first: String, last: String): User
//
    @Insert(onConflict = REPLACE)
    suspend fun insert(Video: Video)

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(Videos: List<Video>)

//    @Delete
//    fun delete(user: User)

}