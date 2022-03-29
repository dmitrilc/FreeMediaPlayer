package com.dimitrilc.freemediaplayer.data.datasources

import com.dimitrilc.freemediaplayer.data.entities.ActiveMediaItem
import kotlinx.coroutines.flow.Flow

interface ActiveMediaRoomDataSource {
    fun insert(activeMediaItem: ActiveMediaItem)

    suspend fun getOnce(): ActiveMediaItem

    fun getObservable(): Flow<ActiveMediaItem>
}