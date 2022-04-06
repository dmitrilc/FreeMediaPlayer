package com.dimitrilc.freemediaplayer.data.worker.globalplaylist

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.room.withTransaction
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem
import com.dimitrilc.freemediaplayer.data.repos.globalplaylist.GlobalPlaylistRepository
import com.dimitrilc.freemediaplayer.data.repos.mediaitem.MediaItemRepository
import com.dimitrilc.freemediaplayer.data.room.database.AppDatabase
import com.dimitrilc.freemediaplayer.data.worker.WORKER_DATA_KEY_CURRENT_PATH
import com.dimitrilc.freemediaplayer.data.worker.WORKER_DATA_KEY_IS_AUDIO
import com.dimitrilc.freemediaplayer.data.worker.WORKER_DATA_KEY_SELECTED_INDEX
import com.dimitrilc.freemediaplayer.data.worker.getActiveMediaWorkerInputData
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class InsertGlobalPlaylistWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val mediaItemRepository: MediaItemRepository,
    private val globalPlaylistRepository: GlobalPlaylistRepository,
    private val appDb: AppDatabase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val currentPath = inputData.getString(WORKER_DATA_KEY_CURRENT_PATH)!!
        val selectedIndex = inputData.getInt(WORKER_DATA_KEY_SELECTED_INDEX, 0)
        val isAudio = inputData.getBoolean(WORKER_DATA_KEY_IS_AUDIO, true)

        val activeMediaData = appDb.withTransaction {
            val items = if (isAudio){
                mediaItemRepository.getAllAudioByLocation(currentPath)
            } else {
                mediaItemRepository.getAllVideoByLocation(currentPath)
            }

            val playlist = items!!.mapIndexed { index, item ->
                GlobalPlaylistItem(
                    mId = index.toLong(),
                    mediaItemId = item.id)
            }

            globalPlaylistRepository.replace(playlist)

            getActiveMediaWorkerInputData(
                selectedIndex.toLong(),
                items[selectedIndex].id
            )
        }

        return Result.success(activeMediaData)
    }
}