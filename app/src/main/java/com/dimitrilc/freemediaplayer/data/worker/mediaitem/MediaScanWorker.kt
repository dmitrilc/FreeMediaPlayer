package com.dimitrilc.freemediaplayer.data.worker.mediaitem

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dimitrilc.freemediaplayer.data.repos.mediaitem.MediaItemRepository
import com.dimitrilc.freemediaplayer.data.repos.mediastore.MediaStoreRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

private const val TAG = "MEDIA_SCAN_WORKER"

@HiltWorker
class MediaScanWorker
@AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val mediaItemRepository: MediaItemRepository,
    private val mediaStoreRepository: MediaStoreRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val allAudios = mediaStoreRepository.queryAudios()
        val allVideos = mediaStoreRepository.queryVideos()

        allAudios?.let {
            mediaItemRepository.insertMediaItems(it)
        }

        allVideos?.let {
            mediaItemRepository.insertMediaItems(it)
        }

        return Result.success()
    }

}