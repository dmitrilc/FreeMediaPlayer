package com.dimitrilc.freemediaplayer.data.worker.globalplaylist

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.room.withTransaction
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.dimitrilc.freemediaplayer.data.repos.activemedia.ActiveMediaRepository
import com.dimitrilc.freemediaplayer.data.repos.globalplaylist.GlobalPlaylistRepository
import com.dimitrilc.freemediaplayer.data.room.database.AppDatabase
import com.dimitrilc.freemediaplayer.data.worker.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

//This worker should not be used for recyclerview onMove/onMoved. Must be used with onSelectedItemChanged
@HiltWorker
class MoveGlobalPlaylistItemPositionsWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val workerParams: WorkerParameters,
    private val globalPlaylistRepository: GlobalPlaylistRepository,
    private val activeMediaRepository: ActiveMediaRepository,
    private val appDb: AppDatabase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val data = Data.Builder()
        val from = inputData.getInt(WORKER_DATA_KEY_FROM, -1)
        val to = inputData.getInt(WORKER_DATA_KEY_TO, -1)

        if (from != -1 && to != -1) {
            appDb.withTransaction {
                val playList = globalPlaylistRepository.getAllOnce()?.toMutableList()
                val movedItem = playList?.get(from)

                if (movedItem != null) {
                    playList.removeAt(from)
                    playList.add(to, movedItem)

                    val reIndexedPlaylist = playList.asSequence()
                        .mapIndexed { index, item ->
                            item.copy(
                                globalPlaylistItemId = index.toLong()
                            )
                        }
                        .toList()

                    val activeMedia = activeMediaRepository.getOnce()
                    globalPlaylistRepository.replace(reIndexedPlaylist)

                    if (activeMedia != null) {
                        if (activeMedia.mediaItemId == movedItem.mediaItemId) {
                            data
                                .putLong(
                                    WORKER_DATA_KEY_GLOBAL_PLAYLIST_INDEX,
                                    reIndexedPlaylist[to].globalPlaylistItemId
                                )
                                .putLong(
                                    WORKER_DATA_KEY_MEDIA_ITEM_ID,
                                    movedItem.mediaItemId
                                )
                        } else {
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
        } else {
            return Result.failure()
        }

        return Result.success(data.build())
    }
}