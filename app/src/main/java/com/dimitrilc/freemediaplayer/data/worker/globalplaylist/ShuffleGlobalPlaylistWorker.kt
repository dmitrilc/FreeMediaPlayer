package com.dimitrilc.freemediaplayer.data.worker.globalplaylist

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.room.withTransaction
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.dimitrilc.freemediaplayer.R
import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem
import com.dimitrilc.freemediaplayer.data.room.database.AppDatabase
import com.dimitrilc.freemediaplayer.data.worker.getActiveMediaWorkerInputData
import com.dimitrilc.freemediaplayer.service.MISC_NOTIFICATION_ID
import com.dimitrilc.freemediaplayer.ui.activities.MISC_NOTIFICATION_CHANNEL_ID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ShuffleGlobalPlaylistWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val appDb: AppDatabase
) : CoroutineWorker(appContext, workerParams)  {

    override suspend fun doWork(): Result {
        val activeMediaData = appDb.withTransaction {
            val playlist = appDb.globalPlaylistDao().getAllOnce()

            playlist?.let {
                val shuffled = playlist.shuffled().mapIndexed { index, item ->
                    GlobalPlaylistItem(
                        globalPlaylistItemId = index.toLong(),
                        mediaItemId = item.mediaItemId
                    )
                }

                val previousActive = appDb.activeMediaDao().getOnce()
                appDb.globalPlaylistDao().replacePlaylist(shuffled)

                previousActive?.let {
                    val newIndexOfPreviousActive = shuffled.indexOfFirst {
                        it.mediaItemId == previousActive.mediaItemId
                    }

                    getActiveMediaWorkerInputData(
                        newIndexOfPreviousActive.toLong(),
                        previousActive.mediaItemId
                    )
                }
            }
        }

        return if (activeMediaData == null){
            Result.failure()
        } else {
            Result.success(activeMediaData)
        }
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