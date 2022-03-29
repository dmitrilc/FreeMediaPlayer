package com.dimitrilc.freemediaplayer.data.datasources

import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import com.dimitrilc.freemediaplayer.data.room.dao.MediaItemDao
import javax.inject.Inject

class MediaItemRoomDataSourceImpl @Inject constructor(private val mediaItemDao: MediaItemDao) : MediaItemRoomDataSource {
    override suspend fun getAllAudio() = mediaItemDao.getAllAudio()
    override suspend fun getAllVideo() = mediaItemDao.getAllVideo()

    override suspend fun getAllAudioByLocation(location: String): List<MediaItem> = mediaItemDao.getAllAudioByLocation(location)
    override suspend fun getAllVideoByLocation(location: String): List<MediaItem> = mediaItemDao.getAllVideoByLocation(location)

    override fun getAllAudioObservable() = mediaItemDao.getAllAudioObservable()
    override fun getAllVideoObservable() = mediaItemDao.getAllVideObservable()

    override suspend fun getById(id: Long) = mediaItemDao.getById(id)

    override suspend fun getActiveMediaItemOnce() = mediaItemDao.getActiveMediaItemOnce()
    override fun getActiveMediaItemObservable() = mediaItemDao.getActiveMediaItemObservable()

    override fun getMediaItemsInGlobalPlaylistObservable() = mediaItemDao.getMediaItemsInGlobalPlaylistObservable()
    override suspend fun getMediaItemsInGlobalPlaylistOnce() = mediaItemDao.getMediaItemsInGlobalPlaylistOnce()
}