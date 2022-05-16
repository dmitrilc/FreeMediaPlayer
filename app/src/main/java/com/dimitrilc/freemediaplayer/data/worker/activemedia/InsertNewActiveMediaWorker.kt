package com.dimitrilc.freemediaplayer.data.worker.activemedia

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import com.dimitrilc.freemediaplayer.data.repos.activemedia.ActiveMediaRepository
import com.dimitrilc.freemediaplayer.data.worker.WORKER_DATA_KEY_GLOBAL_PLAYLIST_INDEX
import com.dimitrilc.freemediaplayer.data.worker.WORKER_DATA_KEY_MEDIA_ITEM_ID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class InsertNewActiveMediaWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val activeMediaRepository: ActiveMediaRepository
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val playlistIndex = inputData.getLong(WORKER_DATA_KEY_GLOBAL_PLAYLIST_INDEX, -1)
        val activeMediaItemId = inputData.getLong(WORKER_DATA_KEY_MEDIA_ITEM_ID, -1)

        if (playlistIndex != -1L && activeMediaItemId != -1L){
            val activeItem = ActiveMedia(
                globalPlaylistPosition = playlistIndex,
                mediaItemId = activeMediaItemId
            )

            activeMediaRepository.insert(activeItem)
        } else {
            Result.failure()
        }

        return Result.success()
    }

}