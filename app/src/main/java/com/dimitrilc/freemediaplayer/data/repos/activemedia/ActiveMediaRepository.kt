package com.dimitrilc.freemediaplayer.data.repos.activemedia

import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaPlaylistPosition
import kotlinx.coroutines.flow.Flow

interface ActiveMediaRepository {
    fun insert(activeMedia: ActiveMedia)
    fun update(activeMedia: ActiveMedia)
    suspend fun getOnce(): ActiveMedia?
    fun getObservable(): Flow<ActiveMedia?>
    fun updatePlaylistPosition(playlistPosition: ActiveMediaPlaylistPosition)
}