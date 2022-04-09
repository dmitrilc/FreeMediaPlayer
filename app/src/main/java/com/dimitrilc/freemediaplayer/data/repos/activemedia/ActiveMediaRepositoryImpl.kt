package com.dimitrilc.freemediaplayer.data.repos.activemedia

import com.dimitrilc.freemediaplayer.data.source.room.activemedia.ActiveMediaRoomDataSource
import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaIsPlaying
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaPlaylistPosition
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaProgress
import javax.inject.Inject

class ActiveMediaRepositoryImpl @Inject constructor(
    private val activeMediaRoomDataSource: ActiveMediaRoomDataSource
    ) : ActiveMediaRepository {
    override fun insert(activeMedia: ActiveMedia) =
        activeMediaRoomDataSource.insert(activeMedia)

    override fun update(activeMedia: ActiveMedia) =
        activeMediaRoomDataSource.update(activeMedia)

    override suspend fun getOnce() =
        activeMediaRoomDataSource.getOnce()

    override fun getObservable() =
        activeMediaRoomDataSource.getObservable()

    override fun updateProgress(progress: ActiveMediaProgress) =
        activeMediaRoomDataSource.updateProgress(progress)

    override fun updatePlaylistPosition(playlistPosition: ActiveMediaPlaylistPosition) =
        activeMediaRoomDataSource.updatePlaylistPosition(playlistPosition)

    override fun updateIsPlaying(activeMediaIsPlaying: ActiveMediaIsPlaying) =
        activeMediaRoomDataSource.updateIsPlaying(activeMediaIsPlaying)

}