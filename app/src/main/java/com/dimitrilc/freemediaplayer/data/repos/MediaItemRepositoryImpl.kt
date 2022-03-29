package com.dimitrilc.freemediaplayer.data.repos

import com.dimitrilc.freemediaplayer.data.datasources.MediaItemRoomDataSource
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import javax.inject.Inject

class MediaItemRepositoryImpl @Inject constructor(
    private val mediaItemRoomDataSource: MediaItemRoomDataSource
    ) : MediaItemRepository {
    override suspend fun getAllAudio() = mediaItemRoomDataSource.getAllAudio()
    override suspend fun getAllVideo() = mediaItemRoomDataSource.getAllVideo()

    override suspend fun getAllAudioByLocation(location: String) = mediaItemRoomDataSource.getAllAudioByLocation(location)
    override suspend fun getAllVideoByLocation(location: String) = mediaItemRoomDataSource.getAllVideoByLocation(location)

    override fun getAllAudioObservable() = mediaItemRoomDataSource.getAllAudioObservable()
    override fun getAllVideoObservable() = mediaItemRoomDataSource.getAllVideoObservable()

    override suspend fun getById(id: Long) = mediaItemRoomDataSource.getById(id)

    override suspend fun getActiveMediaItemOnce() = mediaItemRoomDataSource.getActiveMediaItemOnce()
    override fun getActiveMediaItemObservable() = mediaItemRoomDataSource.getActiveMediaItemObservable()

    override fun getMediaItemsInGlobalPlaylistObservable() = mediaItemRoomDataSource.getMediaItemsInGlobalPlaylistObservable()
    override suspend fun getMediaItemsInGlobalPlaylistOnce() = mediaItemRoomDataSource.getMediaItemsInGlobalPlaylistOnce()

    override fun insertMediaItems(items: Collection<MediaItem>) = mediaItemRoomDataSource.insertMediaItems(items)
}