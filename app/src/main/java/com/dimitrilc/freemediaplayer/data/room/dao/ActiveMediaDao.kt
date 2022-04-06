package com.dimitrilc.freemediaplayer.data.room.dao

import androidx.room.*
import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import kotlinx.coroutines.flow.Flow

@Dao
interface ActiveMediaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(value: ActiveMedia)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(value: ActiveMedia)

    @Query("SELECT * FROM active_media")
    suspend fun getOnce(): ActiveMedia?

    @Query("SELECT * FROM active_media")
    fun getObservable(): Flow<ActiveMedia?>

    @Update(entity = ActiveMedia::class)
    fun updateProgress(activeMediaProgress: ActiveMediaProgress)

    @Update(entity = ActiveMedia::class)
    fun updatePlaylistPosition(activeMediaPlaylistPosition: ActiveMediaPlaylistPosition)

    @Update(entity = ActiveMedia::class)
    fun updateIsPlaying(activeMediaIsPlaying: ActiveMediaIsPlaying)
}