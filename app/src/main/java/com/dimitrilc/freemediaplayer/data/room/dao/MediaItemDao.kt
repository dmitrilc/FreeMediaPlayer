package com.dimitrilc.freemediaplayer.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dimitrilc.freemediaplayer.data.entities.MediaItem

@Dao
interface MediaItemDao {
    @Query("SELECT * FROM media_item WHERE is_audio=1")
    suspend fun getAllAudio(): List<MediaItem>?

    @Query("SELECT * FROM media_item WHERE is_audio=0")
    suspend fun getAllVideo(): List<MediaItem>?

    @Query("SELECT * FROM media_item WHERE is_audio=1")
    fun getAllAudioObservable(): LiveData<List<MediaItem>?>

    @Query("SELECT * FROM media_item WHERE is_audio=0")
    fun getAllVideObservable(): LiveData<List<MediaItem>?>

    @Query("SELECT * FROM media_item WHERE is_audio=1 AND location=:location")
    suspend fun getAllAudioByLocation(location: String): List<MediaItem>?

    @Query("SELECT * FROM media_item WHERE is_audio=0 AND location=:location")
    suspend fun getAllVideoByLocation(location: String): List<MediaItem>?

    @Query("SELECT * FROM media_item WHERE is_audio=1 AND location=:location")
    fun getAllAudioByLocationObservable(location: String): LiveData<List<MediaItem>?>

    @Query("SELECT * FROM media_item WHERE is_audio=0 AND location=:location")
    fun getAllVideoByLocationObservable(location: String): LiveData<List<MediaItem>?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: MediaItem)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(item: Collection<MediaItem>)

    @Query("SELECT * FROM media_item WHERE media_item_id = :id")
    suspend fun getById(id: Long): MediaItem?

    @Query("SELECT * FROM media_item WHERE media_item_id=(SELECT media_item_id FROM active_media LIMIT 1)")
    suspend fun getActiveMediaItemOnce(): MediaItem?

    @Query("SELECT * FROM media_item WHERE media_item_id=(SELECT media_item_id FROM active_media LIMIT 1)")
    fun getActiveMediaItemObservable(): LiveData<MediaItem?>

    //@Query("SELECT * FROM media_item JOIN global_playlist ON media_item.media_item_id=global_playlist.media_item_id ORDER BY global_playlist.global_playlist_item_id")
    @Query("SELECT * FROM media_item WHERE media_item.media_item_id = (SELECT global_playlist_item_id FROM global_playlist ORDER BY global_playlist.global_playlist_item_id)")
    fun getMediaItemsInGlobalPlaylistObservable(): LiveData<List<MediaItem>?>

    //@Query("SELECT * FROM media_item JOIN global_playlist ON media_item.media_item_id=global_playlist.media_item_id ORDER BY global_playlist.global_playlist_item_id")
    @Query("SELECT * FROM media_item WHERE media_item.media_item_id = (SELECT global_playlist_item_id FROM global_playlist ORDER BY global_playlist.global_playlist_item_id)")
    suspend fun getMediaItemsInGlobalPlaylistOnce(): List<MediaItem>?
}