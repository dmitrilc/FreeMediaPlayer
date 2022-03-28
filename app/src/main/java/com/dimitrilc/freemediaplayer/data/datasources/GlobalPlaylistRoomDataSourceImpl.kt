package com.dimitrilc.freemediaplayer.data.datasources

import androidx.lifecycle.LiveData
import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import com.dimitrilc.freemediaplayer.data.room.dao.GlobalPlaylistDao
import javax.inject.Inject

class GlobalPlaylistRoomDataSourceImpl
@Inject constructor(private val globalPlaylistDao: GlobalPlaylistDao)
    : GlobalPlaylistRoomDataSource {
    override fun replacePlaylist(playlist: List<GlobalPlaylistItem>) = globalPlaylistDao.replacePlaylist(playlist)

    override fun getGlobalPlaylistObservable(): LiveData<List<MediaItem>> = globalPlaylistDao.getGlobalPlaylistObservable()

    override suspend fun getOnce() = globalPlaylistDao.getOnce()
}