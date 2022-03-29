package com.dimitrilc.freemediaplayer.data.repos

import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import kotlinx.coroutines.flow.Flow

interface ActiveMediaRepository {
    fun insert(activeMedia: ActiveMedia)
    suspend fun getOnce(): ActiveMedia
    fun getObservable(): Flow<ActiveMedia>
}