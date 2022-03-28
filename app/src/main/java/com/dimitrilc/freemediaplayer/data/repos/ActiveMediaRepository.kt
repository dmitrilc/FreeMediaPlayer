package com.dimitrilc.freemediaplayer.data.repos

import com.dimitrilc.freemediaplayer.data.entities.ActiveMediaItem
import kotlinx.coroutines.flow.Flow

interface ActiveMediaRepository {
    fun insert(activeMediaItem: ActiveMediaItem)
    suspend fun getOnce(): ActiveMediaItem
    fun getObservable(): Flow<ActiveMediaItem>
}