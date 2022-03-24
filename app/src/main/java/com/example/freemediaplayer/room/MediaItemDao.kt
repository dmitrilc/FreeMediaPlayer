package com.example.freemediaplayer.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.freemediaplayer.entities.MediaItem

@Dao
interface MediaItemDao {
    @Query("SELECT * FROM media_items")
    suspend fun getAll(): List<MediaItem>

    @Query("SELECT DISTINCT uri FROM media_items")
    suspend fun getUris(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: MediaItem)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(item: Collection<MediaItem>)

    @Query("SELECT * from media_items WHERE location=(SELECT fullPath from folderItemsUi LIMIT 1) AND isAudio=1")
    fun getCurrentAudioFolderItems(): LiveData<List<MediaItem>>

    @Query("SELECT * from media_items WHERE location=(SELECT fullPath from folderItemsUi LIMIT 1) AND isAudio=0")
    fun getCurrentVideoFolderItems(): LiveData<List<MediaItem>>

    @Query("SELECT * FROM media_items WHERE id = :id")
    suspend fun getById(id: Long): MediaItem

}