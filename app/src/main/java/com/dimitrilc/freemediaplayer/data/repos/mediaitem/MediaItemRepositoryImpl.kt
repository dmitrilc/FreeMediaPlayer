package com.dimitrilc.freemediaplayer.data.repos.mediaitem

import com.dimitrilc.freemediaplayer.data.source.room.mediaitem.MediaItemLocalDataSource
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import javax.inject.Inject

class MediaItemRepositoryImpl @Inject constructor(
    private val mediaItemLocalDataSource: MediaItemLocalDataSource
    ) : MediaItemRepository {
    override suspend fun getAllAudio() = mediaItemLocalDataSource.getAllAudio()
    override suspend fun getAllVideo() = mediaItemLocalDataSource.getAllVideo()

    override fun getAllAudioObservable() = mediaItemLocalDataSource.getAllAudioObservable()
    override fun getAllVideoObservable() = mediaItemLocalDataSource.getAllVideoObservable()

    override suspend fun getAllAudioByLocation(location: String) = mediaItemLocalDataSource.getAllAudioByLocation(location)
    override suspend fun getAllVideoByLocation(location: String) = mediaItemLocalDataSource.getAllVideoByLocation(location)

    override fun getAllAudioByLocationObservable(location: String) = mediaItemLocalDataSource.getAllAudioByLocationObservable(location)
    override fun getAllVideoByLocationObservable(location: String) = mediaItemLocalDataSource.getAllVideoByLocationObservable(location)

    override suspend fun getById(id: Long) = mediaItemLocalDataSource.getById(id)

    override suspend fun getActiveMediaItemOnce() = mediaItemLocalDataSource.getActiveMediaItemOnce()
    override fun getActiveMediaItemObservable() = mediaItemLocalDataSource.getActiveMediaItemObservable()

    override fun getMediaItemsInGlobalPlaylistObservable() = mediaItemLocalDataSource.getMediaItemsInGlobalPlaylistObservable()
    override suspend fun getMediaItemsInGlobalPlaylistOnce() = mediaItemLocalDataSource.getMediaItemsInGlobalPlaylistOnce()

    override fun insertMediaItems(items: Collection<MediaItem>) = mediaItemLocalDataSource.insertMediaItems(items)
}