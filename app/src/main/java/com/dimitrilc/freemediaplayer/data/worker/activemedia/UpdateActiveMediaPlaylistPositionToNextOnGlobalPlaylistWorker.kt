package com.dimitrilc.freemediaplayer.data.worker.activemedia

import android.content.Context
import android.util.Log
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

private const val TAG = "TO_NEXT_WORKER"

@HiltWorker
class UpdateActiveMediaPlaylistPositionToNextOnGlobalPlaylistWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val activeMediaRepository: ActiveMediaRepository,
    private val globalPlaylistRepository: GlobalPlaylistRepository,
    private val appDb: AppDatabase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "TO_NEXT_WORKER")
        appDb.withTransaction {
            val activeMediaPosition = activeMediaRepository.getOnce()?.globalPlaylistPosition
            val playlist = globalPlaylistRepository.getAllOnce()

            if (activeMediaPosition != null
                && playlist != null
                && activeMediaPosition <= playlist.lastIndex
            ){
                val nextItemPos = if (activeMediaPosition == playlist.lastIndex.toLong()){
                    0L
                } else {
                    activeMediaPosition + 1
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