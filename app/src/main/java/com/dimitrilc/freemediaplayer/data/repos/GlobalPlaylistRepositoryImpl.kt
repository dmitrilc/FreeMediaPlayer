package com.dimitrilc.freemediaplayer.data.repos

import com.dimitrilc.freemediaplayer.data.datasources.GlobalPlaylistRoomDataSource
import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem
import javax.inject.Inject

class GlobalPlaylistRepositoryImpl
@Inject constructor(private val globalPlaylistRoomDataSource: GlobalPlaylistRoomDataSource)
    : GlobalPlaylistRepository {
    override fun replacePlaylist(playlist: List<GlobalPlaylistItem>) = globalPlaylistRoomDataSource.replacePlaylist(playlist)
    override fun getAllObservable() = globalPlaylistRoomDataSource.getAllObservable()
    override suspend fun getAllOnce() = globalPlaylistRoomDataSource.getAllOnce()
}