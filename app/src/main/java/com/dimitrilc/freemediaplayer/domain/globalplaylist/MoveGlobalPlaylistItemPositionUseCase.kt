package com.dimitrilc.freemediaplayer.domain.globalplaylist

import com.dimitrilc.freemediaplayer.data.repos.MediaManager
import javax.inject.Inject

class MoveGlobalPlaylistItemPositionUseCase @Inject constructor(
    private val mediaManager: MediaManager
) {
    operator fun invoke(from: Int, to: Int){
        mediaManager.moveGlobalPlaylistItemPosition(from, to)
    }
}