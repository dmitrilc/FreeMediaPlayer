package com.dimitrilc.freemediaplayer.data.datasources

import com.dimitrilc.freemediaplayer.data.entities.ActiveMediaItem
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaDao
import javax.inject.Inject

class ActiveMediaRoomDataSourceImpl
@Inject constructor(private val activeMediaDao: ActiveMediaDao)
    : ActiveMediaRoomDataSource {
    override fun insert(activeMediaItem: ActiveMediaItem) = activeMediaDao.insert(activeMediaItem)
    override suspend fun getOnce() = activeMediaDao.getOnce()
    override fun getObservable() = activeMediaDao.getObservable()
}