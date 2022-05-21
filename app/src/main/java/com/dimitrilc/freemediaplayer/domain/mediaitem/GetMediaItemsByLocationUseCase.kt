package com.dimitrilc.freemediaplayer.domain.mediaitem

import com.dimitrilc.freemediaplayer.data.repos.mediaitem.MediaItemRepository
import javax.inject.Inject

class GetMediaItemsByLocationUseCase @Inject constructor(
    private val mediaItemRepository: MediaItemRepository
) {
    operator fun invoke(isAudio: Boolean, location: String) = if (isAudio){
        mediaItemRepository.getAllAudioByLocationObservable(location)
    } else {
        mediaItemRepository.getAllVideoByLocationObservable(location)
    }

}