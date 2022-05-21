package com.dimitrilc.freemediaplayer.domain.mediastore

import android.graphics.Bitmap
import com.dimitrilc.freemediaplayer.data.repos.mediaitem.MediaItemRepository
import com.dimitrilc.freemediaplayer.data.repos.mediastore.MediaStoreRepository
import javax.inject.Inject

class GetThumbByMediaIdUseCase @Inject constructor(
    private val mediaStoreRepository: MediaStoreRepository,
    private val mediaItemRepository: MediaItemRepository
) {
    suspend operator fun invoke(mediaId: Long): Bitmap? {
        val mediaItem = mediaItemRepository.getById(mediaId)
        return mediaStoreRepository.getThumbnail(
            mediaItem?.albumArtUri,
            if (mediaItem?.isAudio == true) mediaItem.mediaItemId else null
        )
    }
}