package com.dimitrilc.freemediaplayer.data.repos

import androidx.work.Operation
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import java.util.*

interface MediaManager {
    fun insertGlobalPlaylistAndActiveItem(currentPath: String, selectedIndex: Int, isAudio: Boolean): UUID
    fun updateGlobalPlaylistAndActiveMedia(playlist: List<MediaItem>, activeItem: MediaItem)
    fun shuffleGlobalPlaylistAndActiveItem()
    fun activateMediaScanWorker()
    fun updateActiveMediaPlaylistPositionToNextOnGlobalPlaylist()
    fun updateActiveMediaPlaylistPositionToPreviousOnGlobalPlaylist()
}