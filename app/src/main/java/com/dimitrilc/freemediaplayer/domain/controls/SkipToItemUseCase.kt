package com.dimitrilc.freemediaplayer.domain.controls

import com.dimitrilc.freemediaplayer.domain.activemedia.UpdateActiveMediaByObjectUseCase
import javax.inject.Inject

class SkipToItemUseCase @Inject constructor(
    updateActiveMediaByObjectUseCase: UpdateActiveMediaByObjectUseCase
) {
    operator fun invoke(playlistPos: Long){

    }
}