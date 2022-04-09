package com.dimitrilc.freemediaplayer.data.source.room.activemedia

import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaDao
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaIsPlaying
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaPlaylistPosition
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaProgress
import javax.inject.Inject

class ActiveMediaRoomDataSourceImpl
@Inject constructor(private val activeMediaDao: ActiveMediaDao)
    : ActiveMediaRoomDataSource {
    override fun insert(activeMedia: ActiveMedia) =
        activeMediaDao.insert(activeMedia)

    override fun update(activeMedia: ActiveMedia) =
        activeMediaDao.update(activeMedia)

    override suspend fun getOnce() =
        activeMediaDao.getOnce()

    override fun getObservable() =
        activeMediaDao.getObservable()

    override fun updateProgress(activeMediaProgress: ActiveMediaProgress) =
        activeMediaDao.updateProgress(activeMediaProgress)

    override fun updatePlaylistPosition(playlistPosition: ActiveMediaPlaylistPosition) =
        activeMediaDao.updatePlaylistPosition(playlistPosition)

    override fun updateIsPlaying(activeMediaIsPlaying: ActiveMediaIsPlaying) =
        activeMediaDao.updateIsPlaying(activeMediaIsPlaying)
}