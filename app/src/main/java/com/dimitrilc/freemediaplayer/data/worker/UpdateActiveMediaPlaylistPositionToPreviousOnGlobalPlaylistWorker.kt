package com.dimitrilc.freemediaplayer.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.room.withTransaction
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import com.dimitrilc.freemediaplayer.data.repos.activemedia.ActiveMediaRepository
import com.dimitrilc.freemediaplayer.data.repos.globalplaylist.GlobalPlaylistRepository
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaPlaylistPosition
import com.dimitrilc.freemediaplayer.data.room.database.AppDatabase
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

}