package com.dimitrilc.freemediaplayer.domain

import com.dimitrilc.freemediaplayer.data.repos.MediaManager
import javax.inject.Inject

class GenerateGlobalPlaylistAndActiveItemUseCase @Inject constructor(private val mediaManager: MediaManager) {
    operator fun invoke(currentPath: String, selectedIndex: Int, isAudio: Boolean) =
        mediaManager.insertGlobalPlaylistAndActiveItem(currentPath, selectedIndex, isAudio)
}