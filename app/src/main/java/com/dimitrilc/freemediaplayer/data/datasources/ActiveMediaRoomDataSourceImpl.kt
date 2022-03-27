package com.dimitrilc.freemediaplayer.data.datasources

import com.dimitrilc.freemediaplayer.data.entities.ActiveMediaItem
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaItemDao
import javax.inject.Inject

class ActiveMediaRoomDataSourceImpl
@Inject constructor(private val activeMediaItemDao: ActiveMediaItemDao)
    : ActiveMediaRoomDataSource {
    override fun insert(activeMediaItem: ActiveMediaItem) = activeMediaItemDao.insert(activeMediaItem)
}