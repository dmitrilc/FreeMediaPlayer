package com.dimitrilc.freemediaplayer.data.repos.activemedia

import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaPlaylistPosition
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaProgress
import kotlinx.coroutines.flow.Flow

interface ActiveMediaRepository {
    fun insert(activeMedia: ActiveMedia)
    fun update(activeMedia: ActiveMedia)
    suspend fun getOnce(): ActiveMedia?
    fun getObservable(): Flow<ActiveMedia?>
    fun updateProgress(progress: ActiveMediaProgress)
    fun updatePlaylistPosition(playlistPosition: ActiveMediaPlaylistPosition)
}