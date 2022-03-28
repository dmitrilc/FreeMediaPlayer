package com.dimitrilc.freemediaplayer.data.repos

import androidx.lifecycle.LiveData
import com.dimitrilc.freemediaplayer.data.datasources.ActiveMediaRoomDataSource
import com.dimitrilc.freemediaplayer.data.entities.ActiveMediaItem
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import javax.inject.Inject

class ActiveMediaRepositoryImpl @Inject constructor(
    private val activeMediaRoomDataSource: ActiveMediaRoomDataSource
    ) : ActiveMediaRepository {
    override fun insert(activeMediaItem: ActiveMediaItem) = activeMediaRoomDataSource.insert(activeMediaItem)

    //override fun getMediaItemObservable() = activeMediaRoomDataSource.getMediaItemObservable()

    override suspend fun getOnce() = activeMediaRoomDataSource.getOnce()

    override fun getObservable() = activeMediaRoomDataSource.getObservable()

    //override suspend fun getActiveMediaItemOnce() = activeMediaRoomDataSource.getActiveMediaItemOnce()
}