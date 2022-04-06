package com.dimitrilc.freemediaplayer.domain.mediaitem

import com.dimitrilc.freemediaplayer.data.repos.mediaitem.MediaItemRepository
import javax.inject.Inject

class GetActiveMediaItemObservableUseCase @Inject constructor(
    private val mediaItemRepository: MediaItemRepository
) {
    operator fun invoke() = mediaItemRepository.getActiveMediaItemObservable()
}