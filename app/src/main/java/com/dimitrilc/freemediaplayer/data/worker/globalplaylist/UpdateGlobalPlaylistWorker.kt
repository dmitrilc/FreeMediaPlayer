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
        val mediaItemIdList = inputData.getLongArray(WORKER_DATA_KEY_MEDIA_ITEM_ID_LIST)!!

        val globalPlaylist = mediaItemIdList.mapIndexed { index, id ->
            GlobalPlaylistItem(
                mId = index.toLong(),
                mediaItemId = id)
        }

            //Needs to run sequentially because of foreign key constraint
        globalPlaylistRepository.replace(globalPlaylist)

/*            val index = mediaItemIdList.indexOf(activeMediaItemId)

            val newActiveItem = ActiveMedia(
                globalPlaylistPosition = index.toLong(),
                mediaItemId = activeMediaItemId
            )

            activeMediaRepository.insert(newActiveItem)*/


        return Result.success()
    }
}