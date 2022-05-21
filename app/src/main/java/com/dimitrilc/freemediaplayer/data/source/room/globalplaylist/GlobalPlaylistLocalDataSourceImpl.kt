package com.dimitrilc.freemediaplayer.data.source.room.globalplaylist

import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem
import com.dimitrilc.freemediaplayer.data.room.dao.GlobalPlaylistDao
import javax.inject.Inject

class GlobalPlaylistLocalDataSourceImpl
@Inject constructor(private val globalPlaylistDao: GlobalPlaylistDao)
    : GlobalPlaylistLocalDataSource {
    override fun replace(playlist: List<GlobalPlaylistItem>) = globalPlaylistDao.replacePlaylist(playlist)
    override fun removeItem(item: GlobalPlaylistItem) = globalPlaylistDao.remove(item)
    override fun getAllObservable() = globalPlaylistDao.getAllObservable()
    override suspend fun getAllOnce() = globalPlaylistDao.getAllOnce()
    override suspend fun count() = globalPlaylistDao.count()
    override fun removeAtPosition(position: Long) = globalPlaylistDao.removeByPosition(position)
    override suspend fun getByPosition(pos: Int) = globalPlaylistDao.getByPosition(pos)
    override fun delete(vararg items: GlobalPlaylistItem) = globalPlaylistDao.delete(*items)
}