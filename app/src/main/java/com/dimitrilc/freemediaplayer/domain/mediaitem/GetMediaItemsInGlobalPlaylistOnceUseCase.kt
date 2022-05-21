package com.dimitrilc.freemediaplayer.domain.mediaitem

import com.dimitrilc.freemediaplayer.data.repos.mediaitem.MediaItemRepository
import javax.inject.Inject

class GetMediaItemsInGlobalPlaylistOnceUseCase @Inject constructor(private val mediaItemRepository: MediaItemRepository) {
    suspend operator fun invoke() = mediaItemRepository.getMediaItemsInGlobalPlaylistOnce()
}