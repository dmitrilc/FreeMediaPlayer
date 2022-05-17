package com.dimitrilc.freemediaplayer.data.repos.globalplaylist

import androidx.lifecycle.LiveData
import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem

interface GlobalPlaylistRepository {
    fun replace(playlist: List<GlobalPlaylistItem>)
    fun removeItem(item: GlobalPlaylistItem)
    fun getAllObservable(): LiveData<List<GlobalPlaylistItem>?>
    suspend fun getAllOnce(): List<GlobalPlaylistItem>?
    suspend fun count(): Long?
    fun removeItemAtPosition(position: Long)
    suspend fun getByPosition(pos: Int): GlobalPlaylistItem?
    fun delete(vararg items: GlobalPlaylistItem)
}