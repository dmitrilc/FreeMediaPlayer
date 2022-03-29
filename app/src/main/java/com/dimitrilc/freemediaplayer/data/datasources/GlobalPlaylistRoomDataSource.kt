package com.dimitrilc.freemediaplayer.data.datasources

import androidx.lifecycle.LiveData
import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem

interface GlobalPlaylistRoomDataSource {
    fun replacePlaylist(playlist: List<GlobalPlaylistItem>)
    fun getAllObservable(): LiveData<List<GlobalPlaylistItem>>
    suspend fun getAllOnce(): List<GlobalPlaylistItem>
}