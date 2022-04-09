package com.dimitrilc.freemediaplayer.data.source.room.mediaitem

import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import com.dimitrilc.freemediaplayer.data.room.dao.MediaItemDao
import javax.inject.Inject

class MediaItemRoomDataSourceImpl @Inject constructor(private val mediaItemDao: MediaItemDao) :
    MediaItemRoomDataSource {
    override suspend fun getAllAudio() = mediaItemDao.getAllAudio()
    override suspend fun getAllVideo() = mediaItemDao.getAllVideo()

    override fun getAllAudioObservable() = mediaItemDao.getAllAudioObservable()
    override fun getAllVideoObservable() = mediaItemDao.getAllVideObservable()

    override suspend fun getAllAudioByLocation(location: String) = mediaItemDao.getAllAudioByLocation(location)
    override suspend fun getAllVideoByLocation(location: String) = mediaItemDao.getAllVideoByLocation(location)

    override fun getAllAudioByLocationObservable(location: String) = mediaItemDao.getAllAudioByLocationObservable(location)
    override fun getAllVideoByLocationObservable(location: String) = mediaItemDao.getAllVideoByLocationObservable(location)

    override suspend fun getById(id: Long) = mediaItemDao.getById(id)

    override suspend fun getActiveMediaItemOnce() = mediaItemDao.getActiveMediaItemOnce()
    override fun getActiveMediaItemObservable() = mediaItemDao.getActiveMediaItemObservable()

    override fun getMediaItemsInGlobalPlaylistObservable() = mediaItemDao.getMediaItemsInGlobalPlaylistObservable()
    override suspend fun getMediaItemsInGlobalPlaylistOnce() = mediaItemDao.getMediaItemsInGlobalPlaylistOnce()

    override fun insertMediaItems(items: Collection<MediaItem>) = mediaItemDao.insertAll(items)
}