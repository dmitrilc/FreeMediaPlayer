package com.dimitrilc.freemediaplayer.domain.mediaitem

import androidx.lifecycle.LiveData
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import com.dimitrilc.freemediaplayer.data.repos.mediaitem.MediaItemRepository
import javax.inject.Inject

class GetAllMediaItemsObservableUseCase @Inject constructor(
    private val mediaItemRepository: MediaItemRepository
) {
    operator fun invoke(isAudio: Boolean): LiveData<List<MediaItem>?> = if (isAudio) {
        mediaItemRepository.getAllAudioObservable()
    } else {
        mediaItemRepository.getAllVideoObservable()
    }
}