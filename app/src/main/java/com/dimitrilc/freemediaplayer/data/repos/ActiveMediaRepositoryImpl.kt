package com.dimitrilc.freemediaplayer.data.repos

import com.dimitrilc.freemediaplayer.data.datasources.ActiveMediaRoomDataSource
import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import javax.inject.Inject

class ActiveMediaRepositoryImpl @Inject constructor(
    private val activeMediaRoomDataSource: ActiveMediaRoomDataSource
    ) : ActiveMediaRepository {
    override fun insert(activeMedia: ActiveMedia) = activeMediaRoomDataSource.insert(activeMedia)
    override suspend fun getOnce() = activeMediaRoomDataSource.getOnce()
    override fun getObservable() = activeMediaRoomDataSource.getObservable()
}