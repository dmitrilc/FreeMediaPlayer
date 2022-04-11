package com.dimitrilc.freemediaplayer.domain.mediastore

import com.dimitrilc.freemediaplayer.data.repos.MediaManager
import com.dimitrilc.freemediaplayer.data.repos.mediastore.MediaStoreRepository
import javax.inject.Inject

class ScanForMediaFilesUseCase @Inject constructor(
    private val mediaManager: MediaManager
) {
    operator fun invoke() = mediaManager.activateMediaScanWorker()
}