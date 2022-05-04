package com.dimitrilc.freemediaplayer.data.worker.globalplaylist

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dimitrilc.freemediaplayer.data.source.room.globalplaylist.GlobalPlaylistLocalDataSource
import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem
import com.dimitrilc.freemediaplayer.data.worker.WORKER_DATA_KEY_GLOBAL_PLAYLIST_INDEX
import com.dimitrilc.freemediaplayer.data.worker.WORKER_DATA_KEY_MEDIA_ITEM_ID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RemoveGlobalPlaylistItemWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val globalPlaylistLocalDataSource: GlobalPlaylistLocalDataSource,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val index = inputData.getLong(WORKER_DATA_KEY_GLOBAL_PLAYLIST_INDEX, 0)
        val mediaItemId = inputData.getLong(WORKER_DATA_KEY_MEDIA_ITEM_ID, 0)

        globalPlaylistLocalDataSource.removeItem(
            GlobalPlaylistItem(
                index,
                mediaItemId
            )
        )

        return Result.success()
    }
}