package com.dimitrilc.freemediaplayer.data.worker.globalplaylist

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem
import com.dimitrilc.freemediaplayer.data.repos.globalplaylist.GlobalPlaylistRepository
import com.dimitrilc.freemediaplayer.data.worker.WORKER_DATA_KEY_MEDIA_ITEM_ID_LIST
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UpdateGlobalPlaylistWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val globalPlaylistRepository: GlobalPlaylistRepository,
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val mediaItemIdList = inputData.getLongArray(WORKER_DATA_KEY_MEDIA_ITEM_ID_LIST)

        return if (mediaItemIdList != null){
            val globalPlaylist = mediaItemIdList.mapIndexed { index, id ->
                GlobalPlaylistItem(
                    globalPlaylistItemId = index.toLong(),
                    mediaItemId = id)
            }

            globalPlaylistRepository.replace(globalPlaylist)

            Result.success()
        } else {
            Result.failure()
        }
    }
}