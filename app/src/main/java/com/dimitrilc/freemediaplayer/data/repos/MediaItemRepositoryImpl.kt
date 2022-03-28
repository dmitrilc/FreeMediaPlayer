package com.dimitrilc.freemediaplayer.data.repos

import androidx.room.withTransaction
import com.dimitrilc.freemediaplayer.data.datasources.MediaItemRoomDataSource
import com.dimitrilc.freemediaplayer.data.entities.ActiveMediaItem
import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import com.dimitrilc.freemediaplayer.data.room.database.AppDatabase
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
}