package com.dimitrilc.freemediaplayer.domain.mediastore

import com.dimitrilc.freemediaplayer.data.repos.mediastore.MediaStoreRepository
import javax.inject.Inject

class GetThumbByUriUseCase @Inject constructor(
    private val mediaStoreRepository: MediaStoreRepository
) {
    operator fun invoke(artUri: String?, videoId: Long?) = mediaStoreRepository.getThumbnail(artUri, videoId)
}