package com.dimitrilc.freemediaplayer.data.repos

import androidx.lifecycle.LiveData
import com.dimitrilc.freemediaplayer.data.datasources.GlobalPlaylistRoomDataSource
import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import javax.inject.Inject

class GlobalPlaylistRepositoryImpl
@Inject constructor(private val globalPlaylistRoomDataSource: GlobalPlaylistRoomDataSource)
    : GlobalPlaylistRepository {
    override fun replacePlaylist(playlist: List<GlobalPlaylistItem>) = globalPlaylistRoomDataSource.replacePlaylist(playlist)

    override fun getGlobalPlaylistObservable() = globalPlaylistRoomDataSource.getGlobalPlaylistObservable()

    override suspend fun getOnce() = globalPlaylistRoomDataSource.getOnce()
}