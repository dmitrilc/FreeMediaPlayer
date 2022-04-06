package com.dimitrilc.freemediaplayer.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dimitrilc.freemediaplayer.data.entities.MediaItem

@Dao
interface MediaItemDao {
    @Query("SELECT * FROM media_items WHERE isAudio=1")
    suspend fun getAllAudio(): List<MediaItem>?

    @Query("SELECT * FROM media_items WHERE isAudio=0")
    suspend fun getAllVideo(): List<MediaItem>?

    @Query("SELECT * FROM media_items WHERE isAudio=1")
    fun getAllAudioObservable(): LiveData<List<MediaItem>?>

    @Query("SELECT * FROM media_items WHERE isAudio=0")
    fun getAllVideObservable(): LiveData<List<MediaItem>?>

    @Query("SELECT * FROM media_items WHERE isAudio=1 AND location=:location")
    suspend fun getAllAudioByLocation(location: String): List<MediaItem>?

    @Query("SELECT * FROM media_items WHERE isAudio=0 AND location=:location")
    suspend fun getAllVideoByLocation(location: String): List<MediaItem>?

    @Query("SELECT * FROM media_items WHERE isAudio=1 AND location=:location")
    fun getAllAudioByLocationObservable(location: String): LiveData<List<MediaItem>?>

    @Query("SELECT * FROM media_items WHERE isAudio=0 AND location=:location")
    fun getAllVideoByLocationObservable(location: String): LiveData<List<MediaItem>?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: MediaItem)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(item: Collection<MediaItem>)

    @Query("SELECT * FROM media_items WHERE id = :id")
    suspend fun getById(id: Long): MediaItem?

    @Query("SELECT * FROM media_items WHERE id=(SELECT mediaItemId FROM active_media LIMIT 1)")
    suspend fun getActiveMediaItemOnce(): MediaItem?

    @Query("SELECT * FROM media_items WHERE id=(SELECT mediaItemId FROM active_media LIMIT 1)")
    fun getActiveMediaItemObservable(): LiveData<MediaItem?>

    @Query("SELECT * FROM media_items JOIN global_playlist ON media_items.id=global_playlist.mediaItemId ORDER BY global_playlist.mId")
    fun getMediaItemsInGlobalPlaylistObservable(): LiveData<List<MediaItem>?>

    @Query("SELECT * FROM media_items JOIN global_playlist ON media_items.id=global_playlist.mediaItemId ORDER BY global_playlist.mId")
    suspend fun getMediaItemsInGlobalPlaylistOnce(): List<MediaItem>?
}