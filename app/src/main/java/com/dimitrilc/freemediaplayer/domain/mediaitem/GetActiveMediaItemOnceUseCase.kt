package com.dimitrilc.freemediaplayer.domain.mediaitem

import com.dimitrilc.freemediaplayer.data.repos.mediaitem.MediaItemRepository
import javax.inject.Inject

class GetActiveMediaItemOnceUseCase @Inject constructor(private val mediaItemRepository: MediaItemRepository) {
    suspend operator fun invoke() = mediaItemRepository.getActiveMediaItemOnce()
}