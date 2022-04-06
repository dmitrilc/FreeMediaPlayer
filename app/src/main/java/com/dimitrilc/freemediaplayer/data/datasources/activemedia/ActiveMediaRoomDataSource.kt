package com.dimitrilc.freemediaplayer.data.datasources.activemedia

import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaIsPlaying
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaPlaylistPosition
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaProgress
import kotlinx.coroutines.flow.Flow

interface ActiveMediaRoomDataSource {
    fun insert(activeMedia: ActiveMedia)
    fun update(activeMedia: ActiveMedia)
    suspend fun getOnce(): ActiveMedia?
    fun getObservable(): Flow<ActiveMedia?>
    fun updateProgress(activeMediaProgress: ActiveMediaProgress)
    fun updatePlaylistPosition(playlistPosition: ActiveMediaPlaylistPosition)
    fun updateIsPlaying(activeMediaIsPlaying: ActiveMediaIsPlaying)
}