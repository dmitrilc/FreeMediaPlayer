package com.dimitrilc.freemediaplayer.data.repos

import androidx.lifecycle.LiveData
import com.dimitrilc.freemediaplayer.data.entities.MediaItem

interface MediaItemRepository {
    suspend fun getAllAudio(): List<MediaItem>
    suspend fun getAllVideo(): List<MediaItem>
    suspend fun getAllAudioByLocation(location: String): List<MediaItem>
    suspend fun getAllVideoByLocation(location: String): List<MediaItem>
    fun getAllAudioObservable(): LiveData<List<MediaItem>>
    fun getAllVideoObservable(): LiveData<List<MediaItem>>
    suspend fun updateGlobalPlaylistAndActiveItem(currentPath: String, selectedIndex: Int)
}