package com.dimitrilc.freemediaplayer.data.datasources

import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import kotlinx.coroutines.flow.Flow

interface ActiveMediaRoomDataSource {
    fun insert(activeMedia: ActiveMedia)
    suspend fun getOnce(): ActiveMedia
    fun getObservable(): Flow<ActiveMedia>
}