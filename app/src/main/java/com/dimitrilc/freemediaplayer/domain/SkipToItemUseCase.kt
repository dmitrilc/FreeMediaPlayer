package com.dimitrilc.freemediaplayer.domain

import com.dimitrilc.freemediaplayer.domain.activemedia.UpdateActiveMediaUseCase
import javax.inject.Inject

class SkipToItemUseCase @Inject constructor(
    updateActiveMediaUseCase: UpdateActiveMediaUseCase
) {
    operator fun invoke(playlistPos: Long){

    }
}