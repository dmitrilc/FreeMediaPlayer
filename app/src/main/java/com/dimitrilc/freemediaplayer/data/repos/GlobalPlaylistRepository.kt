package com.dimitrilc.freemediaplayer.data.repos

import androidx.lifecycle.LiveData
import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem
import com.dimitrilc.freemediaplayer.data.entities.MediaItem

interface GlobalPlaylistRepository {
    fun replacePlaylist(playlist: List<GlobalPlaylistItem>)

    fun getGlobalPlaylistObservable(): LiveData<List<MediaItem>>

    suspend fun getOnce(): List<MediaItem>
}