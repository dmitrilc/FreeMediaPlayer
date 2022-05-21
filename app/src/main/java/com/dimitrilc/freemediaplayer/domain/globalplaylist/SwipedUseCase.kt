package com.dimitrilc.freemediaplayer.domain.globalplaylist

import com.dimitrilc.freemediaplayer.data.repos.MediaManager
import javax.inject.Inject

class SwipedUseCase @Inject constructor(private val mediaManager: MediaManager) {
    operator fun invoke(position: Long) =
        mediaManager.removeGlobalPlaylistItemByPositionAndUpdateActiveMedia(position)
}