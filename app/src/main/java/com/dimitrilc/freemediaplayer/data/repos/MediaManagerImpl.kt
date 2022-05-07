package com.dimitrilc.freemediaplayer.data.repos

import android.app.Application
import androidx.room.withTransaction
import androidx.work.*
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import com.dimitrilc.freemediaplayer.data.repos.activemedia.ActiveMediaRepository
import com.dimitrilc.freemediaplayer.data.repos.globalplaylist.GlobalPlaylistRepository
import com.dimitrilc.freemediaplayer.data.room.database.AppDatabase
import com.dimitrilc.freemediaplayer.data.worker.*
import com.dimitrilc.freemediaplayer.data.worker.activemedia.InsertNewActiveMediaWorker
import com.dimitrilc.freemediaplayer.data.worker.activemedia.UpdateActiveMediaPlaylistPositionToNextOnGlobalPlaylistWorker
import com.dimitrilc.freemediaplayer.data.worker.activemedia.UpdateActiveMediaPlaylistPositionToPreviousOnGlobalPlaylistWorker
import com.dimitrilc.freemediaplayer.data.worker.activemedia.UpdateActiveMediaWorker
import com.dimitrilc.freemediaplayer.data.worker.globalplaylist.InsertGlobalPlaylistWorker
import com.dimitrilc.freemediaplayer.data.worker.globalplaylist.ShuffleGlobalPlaylistWorker
import com.dimitrilc.freemediaplayer.data.worker.globalplaylist.UpdateGlobalPlaylistWorker
import com.dimitrilc.freemediaplayer.data.worker.mediaitem.MediaScanWorker
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

const val UPDATE_ACTIVE_WORKER_UUID = "UPDATE_ACTIVE_WORKER_UUID"

class MediaManagerImpl @Inject constructor(
    private val app: Application,
    private val globalPlaylistRepository: GlobalPlaylistRepository,
    private val activeMediaRepository: ActiveMediaRepository,
    private val appDb: AppDatabase
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

        val updateActiveMediaWorkRequest = OneTimeWorkRequestBuilder<UpdateActiveMediaWorker>()
            .build()

        WorkManager.getInstance(app)
            .beginWith(shuffleGlobalPlaylistWorkRequest)
            .then(updateActiveMediaWorkRequest)
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

    override suspend fun onSwiped(position: Long) {
        appDb.withTransaction {
            val playlist = globalPlaylistRepository.getAllOnce()
            val activeMedia = activeMediaRepository.getOnce()

            if (playlist != null && activeMedia != null){
                if (activeMedia.globalPlaylistPosition == position){ //active item removed
                    val nextItemIndex = if (position.toInt() == playlist.lastIndex){
                        0
                    } else {
                        position
                    }

                    withContext(coroutineContext){
                        globalPlaylistRepository.removeItemAtPosition(position)
                    }

                    withContext(coroutineContext){
                        val nextActiveMedia = activeMedia.copy(
                            globalPlaylistPosition = nextItemIndex,
                            mediaItemId = playlist[nextItemIndex.toInt()].mediaItemId
                        )

                        //activeMediaRepository.insert(nextActiveMedia)
                    }
                }
            }
        }
    }
}