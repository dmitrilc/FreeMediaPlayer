package com.dimitrilc.freemediaplayer.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.room.withTransaction
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem
import com.dimitrilc.freemediaplayer.data.repos.activemedia.ActiveMediaRepository
import com.dimitrilc.freemediaplayer.data.repos.globalplaylist.GlobalPlaylistRepository
import com.dimitrilc.freemediaplayer.data.repos.mediaitem.MediaItemRepository
import com.dimitrilc.freemediaplayer.data.room.database.AppDatabase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ShuffleGlobalPlaylistWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val appDb: AppDatabase,
    private val globalPlaylistRepository: GlobalPlaylistRepository,
    private val activeMediaRepository: ActiveMediaRepository,
    private val mediaItemRepository: MediaItemRepository
) : CoroutineWorker(appContext, workerParams)  {

    override suspend fun doWork(): Result {
        val activeMediaData = appDb.withTransaction {
            val playlist = mediaItemRepository.getMediaItemsInGlobalPlaylistOnce()!!
            val previousActive = activeMediaRepository.getOnce()!!

            val shuffled = playlist.shuffled().mapIndexed { index, item ->
                GlobalPlaylistItem(
                    mId = index.toLong(),
                    mediaItemId = item.id
                )
            }

            globalPlaylistRepository.replace(shuffled)

            val newIndexOfPreviousActive = shuffled.indexOfFirst {
                it.mediaItemId == previousActive.mediaItemId
            }

            getActiveMediaWorkerInputData(
                newIndexOfPreviousActive.toLong(),
                previousActive.mediaItemId
            )
        }

        return Result.success(activeMediaData)
    }
}