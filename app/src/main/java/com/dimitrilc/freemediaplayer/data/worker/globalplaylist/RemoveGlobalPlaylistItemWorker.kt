package com.dimitrilc.freemediaplayer.data.worker.globalplaylist

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.dimitrilc.freemediaplayer.R
import com.dimitrilc.freemediaplayer.data.source.room.globalplaylist.GlobalPlaylistLocalDataSource
import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem
import com.dimitrilc.freemediaplayer.data.worker.WORKER_DATA_KEY_GLOBAL_PLAYLIST_INDEX
import com.dimitrilc.freemediaplayer.data.worker.WORKER_DATA_KEY_MEDIA_ITEM_ID
import com.dimitrilc.freemediaplayer.service.MISC_NOTIFICATION_ID
import com.dimitrilc.freemediaplayer.ui.activities.MISC_NOTIFICATION_CHANNEL_ID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RemoveGlobalPlaylistItemWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
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

    override suspend fun getForegroundInfo(): ForegroundInfo {
        //Simple notification that is only shown when this worker is expedited.
        // This will prevent crashing on android pre-12.
        val notification = NotificationCompat.Builder(
            appContext,
            MISC_NOTIFICATION_CHANNEL_ID
        ).setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        return ForegroundInfo(MISC_NOTIFICATION_ID, notification)
    }
}