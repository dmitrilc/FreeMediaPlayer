package com.dimitrilc.freemediaplayer.data.repos

import androidx.lifecycle.LiveData
import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem

interface GlobalPlaylistRepository {
    fun replacePlaylist(playlist: List<GlobalPlaylistItem>)
    fun getAllObservable(): LiveData<List<GlobalPlaylistItem>>
    suspend fun getAllOnce(): List<GlobalPlaylistItem>
}