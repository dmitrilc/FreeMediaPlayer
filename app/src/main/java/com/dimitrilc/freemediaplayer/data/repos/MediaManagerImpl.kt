package com.dimitrilc.freemediaplayer.data.repos

import android.app.Application
import androidx.work.*
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import com.dimitrilc.freemediaplayer.data.worker.*
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
            .map { it.id }
            .toLongArray()

        val updateGlobalPlaylistWorkerData = dataBuilder
            .putLongArray(WORKER_DATA_KEY_MEDIA_ITEM_ID_LIST, mediaItemIdList)
            .build()

        val updateGlobalPlaylistWorkRequest = OneTimeWorkRequestBuilder<UpdateGlobalPlaylistWorker>()
            .setInputData(updateGlobalPlaylistWorkerData)
            .build()

        val activeMediaIndex = mediaItemIdList.indexOf(activeItem.id)

        val updateActiveMediaWorkerData = getActiveMediaWorkerInputData(
            activeMediaIndex.toLong(),
            activeItem.id
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

        val updateActiveMediaWorkRequest = OneTimeWorkRequestBuilder<InsertNewActiveMediaWorker>()
            .build()

        WorkManager.getInstance(app)
            .beginWith(shuffleGlobalPlaylistWorkRequest)
            .then(updateActiveMediaWorkRequest)
            .enqueue()
/*        appDb.withTransaction {
            val playlist = mediaItemRepository.getMediaItemsInGlobalPlaylistOnce()
            val previousActive = activeMediaRepository.getOnce()

            val shuffled = playlist.shuffled().mapIndexed { index, item ->
                GlobalPlaylistItem(
                    mId = index.toLong(),
                    mediaItemId = item.id
                )
            }

            globalPlaylistRepository.replacePlaylist(shuffled)

            val newIndexOfPreviousActive = shuffled.indexOfFirst {
                it.mediaItemId == previousActive.mediaItemId
            }

            val newActive = previousActive.copy(
                globalPlaylistPosition = newIndexOfPreviousActive.toLong()
            )

            activeMediaRepository.insert(newActive)
        }*/
    }

    override fun updateActiveMediaPlaylistPositionToNextOnGlobalPlaylist(){
        val updateActiveMediaPlaylistPositionToNextOnGlobalPlaylistWorkRequest =
            OneTimeWorkRequestBuilder<UpdateActiveMediaPlaylistPositionToNextOnGlobalPlaylistWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()

/*        WorkManager.getInstance(app).enqueueUniqueWork(
            "UpdateActiveMediaPlaylistPosition",
            ExistingWorkPolicy.APPEND,
            updateActiveMediaPlaylistPositionToNextOnGlobalPlaylistWorkRequest
        )*/

        WorkManager.getInstance(app).enqueue(
            updateActiveMediaPlaylistPositionToNextOnGlobalPlaylistWorkRequest
        )
    }

    override fun updateActiveMediaPlaylistPositionToPreviousOnGlobalPlaylist() {
        val updateActiveMediaPlaylistPositionToNextOnGlobalPlaylistWorkRequest =
            OneTimeWorkRequestBuilder<UpdateActiveMediaPlaylistPositionToPreviousOnGlobalPlaylistWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()

        WorkManager.getInstance(app).enqueueUniqueWork(
            "UpdateActiveMediaPlaylistPosition",
            ExistingWorkPolicy.APPEND,
            updateActiveMediaPlaylistPositionToNextOnGlobalPlaylistWorkRequest
        )
    }

    override fun activateMediaScanWorker() {
        val mediaScanWorkRequest = OneTimeWorkRequestBuilder<MediaScanWorker>().build()
        WorkManager.getInstance(app).enqueue(mediaScanWorkRequest)
    }
}