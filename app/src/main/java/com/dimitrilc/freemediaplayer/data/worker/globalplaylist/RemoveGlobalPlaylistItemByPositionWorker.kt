package com.dimitrilc.freemediaplayer.data.worker.globalplaylist

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.room.withTransaction
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.dimitrilc.freemediaplayer.R
import com.dimitrilc.freemediaplayer.data.room.database.AppDatabase
import com.dimitrilc.freemediaplayer.data.worker.WORKER_DATA_KEY_GLOBAL_PLAYLIST_INDEX
import com.dimitrilc.freemediaplayer.data.worker.WORKER_DATA_KEY_MEDIA_ITEM_ID
import com.dimitrilc.freemediaplayer.service.MISC_NOTIFICATION_ID
import com.dimitrilc.freemediaplayer.ui.activities.MISC_NOTIFICATION_CHANNEL_ID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

private const val TAG = "GLOBAL_PLAYLIST_WORKER"

@HiltWorker
class RemoveGlobalPlaylistItemByPositionWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val appDb: AppDatabase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val data = Data.Builder()
        val index = inputData.getLong(WORKER_DATA_KEY_GLOBAL_PLAYLIST_INDEX, -1)

        appDb.withTransaction {
            val playList = appDb.globalPlaylistDao().getAllOnce()?.toMutableList()

            if (playList != null) {
                playList.removeAt(index.toInt())

                val reIndexedPlaylist = playList
                    .mapIndexed { index, item ->
                        item.copy(
                            globalPlaylistItemId = index.toLong()
                        )
                    }

                val activeMedia = appDb.activeMediaDao().getOnce()
                appDb.globalPlaylistDao().replacePlaylist(reIndexedPlaylist)

                if (activeMedia != null) {
                    if (activeMedia.globalPlaylistPosition != index) {
                        val activeGlobalPlaylistItem = reIndexedPlaylist.find {
                            it.mediaItemId == activeMedia.mediaItemId
                        }

                        if (activeGlobalPlaylistItem != null) {
                            data
                                .putLong(
                                    WORKER_DATA_KEY_GLOBAL_PLAYLIST_INDEX,
                                    activeGlobalPlaylistItem.globalPlaylistItemId
                                )
                                .putLong(
                                    WORKER_DATA_KEY_MEDIA_ITEM_ID,
                                    activeGlobalPlaylistItem.mediaItemId
                                )
                        }
                    }
                }
            }
        }
        return Result.success(data.build())
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