package com.dimitrilc.freemediaplayer.data.repos.activemedia

import com.dimitrilc.freemediaplayer.data.source.room.activemedia.ActiveMediaLocalDataSource
import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaPlaylistPosition
import javax.inject.Inject

class ActiveMediaRepositoryImpl @Inject constructor(
    private val activeMediaLocalDataSource: ActiveMediaLocalDataSource
    ) : ActiveMediaRepository {
    override fun insert(activeMedia: ActiveMedia) =
        activeMediaLocalDataSource.insert(activeMedia)

    override fun update(activeMedia: ActiveMedia) =
        activeMediaLocalDataSource.update(activeMedia)

    override suspend fun getOnce() =
        activeMediaLocalDataSource.getOnce()

    override fun getObservable() =
        activeMediaLocalDataSource.getObservable()

    override fun updatePlaylistPosition(playlistPosition: ActiveMediaPlaylistPosition) =
        activeMediaLocalDataSource.updatePlaylistPosition(playlistPosition)
}