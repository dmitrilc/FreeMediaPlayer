package com.dimitrilc.freemediaplayer.data.datasources.globalplaylist

import androidx.lifecycle.LiveData
import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem

interface GlobalPlaylistRoomDataSource {
    fun replace(playlist: List<GlobalPlaylistItem>)
    fun removeItem(item: GlobalPlaylistItem)
    fun getAllObservable(): LiveData<List<GlobalPlaylistItem>?>
    suspend fun getAllOnce(): List<GlobalPlaylistItem>?
    suspend fun count(): Long?
    fun removeAtPosition(position: Long)
}