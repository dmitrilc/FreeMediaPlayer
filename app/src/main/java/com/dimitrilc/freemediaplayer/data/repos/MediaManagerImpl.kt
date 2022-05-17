package com.dimitrilc.freemediaplayer.data.repos

import android.app.Application
import androidx.work.*
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import com.dimitrilc.freemediaplayer.data.worker.*
import com.dimitrilc.freemediaplayer.data.worker.activemedia.InsertNewActiveMediaWorker
import com.dimitrilc.freemediaplayer.data.worker.activemedia.UpdateActiveMediaPlaylistPositionToNextOnGlobalPlaylistWorker
import com.dimitrilc.freemediaplayer.data.worker.activemedia.UpdateActiveMediaPlaylistPositionToPreviousOnGlobalPlaylistWorker
import com.dimitrilc.freemediaplayer.data.worker.activemedia.UpdateActiveMediaWorker
import com.dimitrilc.freemediaplayer.data.worker.globalplaylist.*
import com.dimitrilc.freemediaplayer.data.worker.mediaitem.MediaScanWorker
import java.util.*
import javax.inject.Inject

const val UPDATE_ACTIVE_WORKER_UUID = "UPDATE_ACTIVE_WORKER_UUID"

class MediaManagerImpl @Inject constructor(
    private val app: Application
) : MediaManager {

    private val dataBuilder = Data.Builder()

    override fun insertGlobalPlaylistAndActiveItem(currentPath: String, selectedIndex: Int, isAudio: Boolean): UUID {

        val insertGlobalPlaylistWorkerData = dataBuilder
            .putString(WORKER_DATA_KEY_CURRENT_PATH, currentPath)
            .putInt(WORKER_DATA_KEY_SELECTED_INDEX, selectedIndex)
            .putBoolean(WORKER_DATA_KEY_IS_AUDIO, isAudio)
            .build()

        val insertGlobalPlaylistWorkRequest = OneTimeWorkRequestBuilder<InsertGlobalPlaylistWorker>()
            .setInputData(insertGlobalPlaylistWorkerData)
            .build()

        val updateActiveMediaWorkRequest = OneTimeWorkRequestBuilder<InsertNewActiveMediaWorker>()
            .build()

        WorkManager.getInstance(app)
            .beginWith(insertGlobalPlaylistWorkRequest)
            .then(updateActiveMediaWorkRequest)
            .enqueue()

        return updateActiveMediaWorkRequest.id
    }

    override fun updateGlobalPlaylistAndActiveMedia(playlist: List<MediaItem>, activeItem: MediaItem) {
        val mediaItemIdList = playlist
            .map { it.mediaItemId }
            .toLongArray()

        val updateGlobalPlaylistWorkerData = dataBuilder
            .putLongArray(WORKER_DATA_KEY_MEDIA_ITEM_ID_LIST, mediaItemIdList)
            .build()

        val updateGlobalPlaylistWorkRequest = OneTimeWorkRequestBuilder<UpdateGlobalPlaylistWorker>()
            .setInputData(updateGlobalPlaylistWorkerData)
            .build()

        val activeMediaIndex = mediaItemIdList.indexOf(activeItem.mediaItemId)

        val updateActiveMediaWorkerData = getActiveMediaWorkerInputData(
            activeMediaIndex.toLong(),
            activeItem.mediaItemId
        )

        val updateActiveMediaWorkRequest = OneTimeWorkRequestBuilder<InsertNewActiveMediaWorker>()
            .setInputData(updateActiveMediaWorkerData)
            .build()

        WorkManager.getInstance(app)
            .beginWith(updateGlobalPlaylistWorkRequest)
            .then(updateActiveMediaWorkRequest)
            .enqueue()
    }

    override fun shuffleGlobalPlaylistAndActiveItem() {
        val shuffleGlobalPlaylistWorkRequest = OneTimeWorkRequestBuilder<ShuffleGlobalPlaylistWorker>()
            .build()

        val insertActiveMediaWorkRequest = OneTimeWorkRequestBuilder<InsertNewActiveMediaWorker>()
            .build()

        WorkManager.getInstance(app)
            .beginWith(shuffleGlobalPlaylistWorkRequest)
            .then(insertActiveMediaWorkRequest)
            .enqueue()
    }

    override fun updateActiveMediaPlaylistPositionToNextOnGlobalPlaylist(){
        val updateActiveMediaPlaylistPositionToNextOnGlobalPlaylistWorkRequest =
            OneTimeWorkRequestBuilder<UpdateActiveMediaPlaylistPositionToNextOnGlobalPlaylistWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()

        WorkManager.getInstance(app).enqueue(
            updateActiveMediaPlaylistPositionToNextOnGlobalPlaylistWorkRequest
        )
    }

    override fun updateActiveMediaPlaylistPositionToPreviousOnGlobalPlaylist() {
        val updateActiveMediaPlaylistPositionToPreviousOnGlobalPlaylistWorkRequest =
            OneTimeWorkRequestBuilder<UpdateActiveMediaPlaylistPositionToPreviousOnGlobalPlaylistWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()

        WorkManager.getInstance(app).enqueue(
            updateActiveMediaPlaylistPositionToPreviousOnGlobalPlaylistWorkRequest
        )
    }

    override fun activateMediaScanWorker() {
        val mediaScanWorkRequest = OneTimeWorkRequestBuilder<MediaScanWorker>().build()
        WorkManager.getInstance(app).enqueue(mediaScanWorkRequest)
    }

    override fun moveGlobalPlaylistItemByPositionAndUpdateActiveMedia(from: Int, to: Int) {
        val data = Data.Builder()
            .putInt(WORKER_DATA_KEY_FROM, from)
            .putInt(WORKER_DATA_KEY_TO, to)
            .build()

        val moveGlobalPlaylistItemPositionsWorker =
            OneTimeWorkRequestBuilder<MoveGlobalPlaylistItemPositionsWorker>()
                .setInputData(data)
                .build()

        val updateActiveMediaWorkRequest = OneTimeWorkRequestBuilder<InsertNewActiveMediaWorker>()
            .build()

        WorkManager.getInstance(app)
            .beginWith(moveGlobalPlaylistItemPositionsWorker)
            .then(updateActiveMediaWorkRequest)
            .enqueue()
    }

    override fun removeGlobalPlaylistItemByPositionAndUpdateActiveMedia(pos: Long){
        val data = Data.Builder()
            .putLong(WORKER_DATA_KEY_GLOBAL_PLAYLIST_INDEX, pos)
            .build()

        val removeGlobalPlaylistItemByPositionWorker =
            OneTimeWorkRequestBuilder<RemoveGlobalPlaylistItemByPositionWorker>()
                .setInputData(data)
                .build()

        val insertActiveMediaWorkRequest = OneTimeWorkRequestBuilder<InsertNewActiveMediaWorker>()
            .build()

        WorkManager.getInstance(app)
            .beginWith(removeGlobalPlaylistItemByPositionWorker)
            .then(insertActiveMediaWorkRequest)
            .enqueue()
    }
}