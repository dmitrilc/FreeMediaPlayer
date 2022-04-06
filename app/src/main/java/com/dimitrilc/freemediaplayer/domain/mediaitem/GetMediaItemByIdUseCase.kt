package com.dimitrilc.freemediaplayer.domain.mediaitem

import com.dimitrilc.freemediaplayer.data.repos.mediaitem.MediaItemRepository
import javax.inject.Inject

class GetMediaItemByIdUseCase @Inject constructor(
    private val mediaItemRepository: MediaItemRepository
    ) {
    suspend operator fun invoke(id: Long) = mediaItemRepository.getById(id)
}