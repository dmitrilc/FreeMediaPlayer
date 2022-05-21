package com.dimitrilc.freemediaplayer.domain

import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import com.dimitrilc.freemediaplayer.data.repos.MediaManager
import javax.inject.Inject

class UpdateGlobalPlaylistAndActiveMediaUseCase @Inject constructor(
    private val mediaManager: MediaManager) {
    operator fun invoke(playlist: List<MediaItem>, activeItem: MediaItem) {
        mediaManager.updateGlobalPlaylistAndActiveMedia(playlist, activeItem)
    }
}