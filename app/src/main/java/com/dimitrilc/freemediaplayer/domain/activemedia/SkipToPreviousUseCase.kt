package com.dimitrilc.freemediaplayer.domain.activemedia

import com.dimitrilc.freemediaplayer.data.repos.MediaManager
import javax.inject.Inject

class SkipToPreviousUseCase @Inject constructor(
    private val mediaManager: MediaManager
) {
    operator fun invoke() =
        mediaManager.updateActiveMediaPlaylistPositionToPreviousOnGlobalPlaylist()
}