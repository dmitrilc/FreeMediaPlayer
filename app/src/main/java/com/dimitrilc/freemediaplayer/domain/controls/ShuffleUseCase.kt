package com.dimitrilc.freemediaplayer.domain.controls

import com.dimitrilc.freemediaplayer.data.repos.MediaManager
import javax.inject.Inject

class ShuffleUseCase @Inject constructor(
    private val mediaManager: MediaManager) {

    operator fun invoke(){
        mediaManager.shuffleGlobalPlaylistAndActiveItem()
    }
}