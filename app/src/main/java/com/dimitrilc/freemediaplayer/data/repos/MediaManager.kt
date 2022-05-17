package com.dimitrilc.freemediaplayer.data.repos

import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import java.util.*

interface MediaManager {
    fun insertGlobalPlaylistAndActiveItem(currentPath: String, selectedIndex: Int, isAudio: Boolean): UUID
    fun updateGlobalPlaylistAndActiveMedia(playlist: List<MediaItem>, activeItem: MediaItem)
    fun shuffleGlobalPlaylistAndActiveItem()
    fun activateMediaScanWorker()
    fun updateActiveMediaPlaylistPositionToNextOnGlobalPlaylist()
    fun updateActiveMediaPlaylistPositionToPreviousOnGlobalPlaylist()
    fun moveGlobalPlaylistItemByPositionAndUpdateActiveMedia(from: Int, to: Int)
    fun removeGlobalPlaylistItemByPositionAndUpdateActiveMedia(pos: Long)
}