package com.dimitrilc.freemediaplayer.data.datasources

import androidx.lifecycle.LiveData
import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem
import com.dimitrilc.freemediaplayer.data.entities.MediaItem

interface GlobalPlaylistRoomDataSource {
    fun replacePlaylist(playlist: List<GlobalPlaylistItem>)
    fun getGlobalPlaylistObservable(): LiveData<List<MediaItem>>
    suspend fun getOnce(): List<MediaItem>
}