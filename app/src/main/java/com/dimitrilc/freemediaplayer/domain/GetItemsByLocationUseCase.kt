package com.dimitrilc.freemediaplayer.domain

import androidx.lifecycle.LiveData
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import com.dimitrilc.freemediaplayer.data.repos.MediaItemRepository
import javax.inject.Inject

class GetItemsByLocationUseCase @Inject constructor(
    private val mediaItemRepository: MediaItemRepository
) {
    operator fun invoke(isAudio: Boolean, location: String): LiveData<List<MediaItem>> {
        return if (isAudio){
            mediaItemRepository.getAllAudioByLocationObservable(location)
        } else {
            mediaItemRepository.getAllVideoByLocationObservable(location)
        }
    }
}