package com.dimitrilc.freemediaplayer.data.source.room.activemedia

import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaPlaylistPosition
import kotlinx.coroutines.flow.Flow

interface ActiveMediaLocalDataSource {
    fun insert(activeMedia: ActiveMedia)
    fun update(activeMedia: ActiveMedia)
    suspend fun getOnce(): ActiveMedia?
    fun getObservable(): Flow<ActiveMedia?>
    fun updatePlaylistPosition(playlistPosition: ActiveMediaPlaylistPosition)
}