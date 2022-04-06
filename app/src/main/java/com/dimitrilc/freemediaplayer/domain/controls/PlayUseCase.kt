package com.dimitrilc.freemediaplayer.domain.controls

import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaIsPlaying
import com.dimitrilc.freemediaplayer.domain.activemedia.UpdateActiveMediaIsPlayingUseCase
import javax.inject.Inject

class PlayUseCase @Inject constructor(
    private val updateActiveMediaIsPlayingUseCase: UpdateActiveMediaIsPlayingUseCase
    ) {
    operator fun invoke(){
        updateActiveMediaIsPlayingUseCase(
            ActiveMediaIsPlaying(1, true)
        )
    }
}