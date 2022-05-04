package com.dimitrilc.freemediaplayer.data.source.room.mediaitem

import androidx.lifecycle.LiveData
import com.dimitrilc.freemediaplayer.data.entities.MediaItem

interface MediaItemLocalDataSource {
    suspend fun getAllAudio(): List<MediaItem>?
    suspend fun getAllVideo(): List<MediaItem>?

    fun getAllAudioObservable(): LiveData<List<MediaItem>?>
    fun getAllVideoObservable(): LiveData<List<MediaItem>?>

    suspend fun getAllAudioByLocation(location: String): List<MediaItem>?
    suspend fun getAllVideoByLocation(location: String): List<MediaItem>?

    fun getAllAudioByLocationObservable(location: String): LiveData<List<MediaItem>?>
    fun getAllVideoByLocationObservable(location: String): LiveData<List<MediaItem>?>

    suspend fun getById(id: Long): MediaItem?

    suspend fun getActiveMediaItemOnce(): MediaItem?
    fun getActiveMediaItemObservable(): LiveData<MediaItem?>

    fun getMediaItemsInGlobalPlaylistObservable(): LiveData<List<MediaItem>?>
    suspend fun getMediaItemsInGlobalPlaylistOnce(): List<MediaItem>?

    fun insertMediaItems(items: Collection<MediaItem>)
}