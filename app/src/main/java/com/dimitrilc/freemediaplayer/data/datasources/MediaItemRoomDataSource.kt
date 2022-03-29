package com.dimitrilc.freemediaplayer.data.datasources

import androidx.lifecycle.LiveData
import com.dimitrilc.freemediaplayer.data.entities.MediaItem

interface MediaItemRoomDataSource {
    suspend fun getAllAudio(): List<MediaItem>
    suspend fun getAllVideo(): List<MediaItem>

    suspend fun getAllAudioByLocation(location: String): List<MediaItem>
    suspend fun getAllVideoByLocation(location: String): List<MediaItem>

    fun getAllAudioObservable(): LiveData<List<MediaItem>>
    fun getAllVideoObservable(): LiveData<List<MediaItem>>

    suspend fun getById(id: Long): MediaItem

    suspend fun getActiveMediaItemOnce(): MediaItem
    fun getActiveMediaItemObservable(): LiveData<MediaItem>

    fun getMediaItemsInGlobalPlaylistObservable(): LiveData<List<MediaItem>>
    suspend fun getMediaItemsInGlobalPlaylistOnce(): List<MediaItem>

}