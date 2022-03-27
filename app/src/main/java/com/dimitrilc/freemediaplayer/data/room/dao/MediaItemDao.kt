package com.dimitrilc.freemediaplayer.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dimitrilc.freemediaplayer.data.entities.MediaItem

@Dao
interface MediaItemDao {
    @Query("SELECT * FROM media_items")
    suspend fun getAll(): List<MediaItem>

    @Query("SELECT * FROM media_items WHERE isAudio=1")
    suspend fun getAllAudio(): List<MediaItem>

    @Query("SELECT * FROM media_items WHERE isAudio=0")
    suspend fun getAllVideo(): List<MediaItem>

    @Query("SELECT * FROM media_items WHERE isAudio=1")
    fun getAllAudioObservable(): LiveData<List<MediaItem>>

    @Query("SELECT * FROM media_items WHERE isAudio=0")
    fun getAllVideObservable(): LiveData<List<MediaItem>>

    @Query("SELECT * FROM media_items WHERE isAudio=1 AND location=:location")
    suspend fun getAllAudioByLocation(location: String): List<MediaItem>

    @Query("SELECT * FROM media_items WHERE isAudio=0 AND location=:location")
    suspend fun getAllVideoByLocation(location: String): List<MediaItem>

    @Query("SELECT DISTINCT uri FROM media_items")
    suspend fun getUris(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: MediaItem)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(item: Collection<MediaItem>)

/*    @Query("SELECT * from media_items WHERE location=(SELECT fullPath from folderItemsUiState LIMIT 1) AND isAudio=1")
    fun getCurrentAudioFolderItems(): LiveData<List<MediaItem>>

    @Query("SELECT * from media_items WHERE location=(SELECT fullPath from folderItemsUiState LIMIT 1) AND isAudio=0")
    fun getCurrentVideoFolderItems(): LiveData<List<MediaItem>>*/

    @Query("SELECT * FROM media_items WHERE id = :id")
    suspend fun getById(id: Long): MediaItem

}