package com.dimitrilc.freemediaplayer.data.source.room.activemedia

import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaDao
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaPlaylistPosition
import javax.inject.Inject

class ActiveMediaLocalDataSourceImpl
@Inject constructor(private val activeMediaDao: ActiveMediaDao)
    : ActiveMediaLocalDataSource {
    override fun insert(activeMedia: ActiveMedia) =
        activeMediaDao.insert(activeMedia)

    override fun update(activeMedia: ActiveMedia) =
        activeMediaDao.update(activeMedia)

    override suspend fun getOnce() =
        activeMediaDao.getOnce()

    override fun getObservable() =
        activeMediaDao.getObservable()

    override fun updatePlaylistPosition(playlistPosition: ActiveMediaPlaylistPosition) =
        activeMediaDao.updatePlaylistPosition(playlistPosition)
}