package com.dimitrilc.freemediaplayer.data.repos

import com.dimitrilc.freemediaplayer.data.entities.MediaItem

interface MediaManager {
    suspend fun generateGlobalPlaylistAndActiveItem(currentPath: String, selectedIndex: Int, isAudio: Boolean)
    suspend fun updateGlobalPlaylistAndActiveItem(playlist: List<MediaItem>, activeItem: MediaItem)
    suspend fun shuffleGlobalPlaylistAndActiveItem()
    fun activateMediaScanWorker()
}