package com.dimitrilc.freemediaplayer.domain.globalplaylist

import com.dimitrilc.freemediaplayer.data.repos.MediaManager
import com.dimitrilc.freemediaplayer.domain.activemedia.GetActiveMediaOnceUseCase
import com.dimitrilc.freemediaplayer.domain.activemedia.InsertActiveMediaUseCase
import javax.inject.Inject

class SwipedUseCase @Inject constructor(
    private val mediaManager: MediaManager
    ) {

    suspend operator fun invoke(position: Long){
        mediaManager.onSwiped(position)

        //mediaManager.onSwiped(position)
        //get playlist
        //get activeMedia
        //if activeMedia is Swiped, updateActiveMedia with next song
    }
}