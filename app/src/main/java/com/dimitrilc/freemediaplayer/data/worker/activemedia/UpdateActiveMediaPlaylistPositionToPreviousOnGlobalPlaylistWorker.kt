package com.dimitrilc.freemediaplayer.data.worker.activemedia

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.room.withTransaction
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.dimitrilc.freemediaplayer.R
import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import com.dimitrilc.freemediaplayer.data.repos.activemedia.ActiveMediaRepository
import com.dimitrilc.freemediaplayer.data.repos.globalplaylist.GlobalPlaylistRepository
import com.dimitrilc.freemediaplayer.data.room.database.AppDatabase
import com.dimitrilc.freemediaplayer.service.MISC_NOTIFICATION_ID
import com.dimitrilc.freemediaplayer.ui.activities.MISC_NOTIFICATION_CHANNEL_ID
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UpdateActiveMediaPlaylistPositionToPreviousOnGlobalPlaylistWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val activeMediaRepository: ActiveMediaRepository,
    private val globalPlaylistRepository: GlobalPlaylistRepository,
    private val appDb: AppDatabase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        appDb.withTransaction {
            val activeMediaPosition = activeMediaRepository.getOnce()?.globalPlaylistPosition
            val playlist = globalPlaylistRepository.getAllOnce()

            if (activeMediaPosition != null
                && playlist != null
                && activeMediaPosition <= playlist.lastIndex
            ){
                val nextItemPos = if (activeMediaPosition == 0L){
                    playlist.lastIndex.toLong()
                } else {
                    activeMediaPosition - 1
                }

                activeMediaRepository.update(
                    ActiveMedia(
                        globalPlaylistPosition = nextItemPos,
                        mediaItemId = playlist[nextItemPos.toInt()].mediaItemId
                    )
                )
            }
        }

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