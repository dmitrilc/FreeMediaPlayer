package com.dimitrilc.freemediaplayer.data.datasources

import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem
import com.dimitrilc.freemediaplayer.data.room.dao.GlobalPlaylistDao
import javax.inject.Inject

class GlobalPlaylistRoomDataSourceImpl
@Inject constructor(private val globalPlaylistDao: GlobalPlaylistDao)
    : GlobalPlaylistRoomDataSource {
    override fun replacePlaylist(playlist: List<GlobalPlaylistItem>) = globalPlaylistDao.replacePlaylist(playlist)
    override fun getAllObservable() = globalPlaylistDao.getAllObservable()
    override suspend fun getAllOnce() = globalPlaylistDao.getAllOnce()
}