package com.dimitrilc.freemediaplayer.data.repos.globalplaylist

import android.app.Application
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.dimitrilc.freemediaplayer.data.datasources.globalplaylist.GlobalPlaylistRoomDataSource
import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem
import com.dimitrilc.freemediaplayer.data.worker.*
import com.dimitrilc.freemediaplayer.data.worker.globalplaylist.RemoveGlobalPlaylistItemWorker
import javax.inject.Inject

class GlobalPlaylistRepositoryImpl
@Inject constructor(
    private val globalPlaylistRoomDataSource: GlobalPlaylistRoomDataSource,
    private val app: Application
    )
    : GlobalPlaylistRepository {
    override fun replace(playlist: List<GlobalPlaylistItem>) =
        globalPlaylistRoomDataSource.replace(playlist)

    override fun removeItem(item: GlobalPlaylistItem) {
        val data = Data.Builder()
            .putLong(WORKER_DATA_KEY_GLOBAL_PLAYLIST_INDEX, item.mId)
            .putLong(WORKER_DATA_KEY_MEDIA_ITEM_ID, item.mediaItemId)
            .build()

        val removeGlobalPlaylistItemWorkRequest = OneTimeWorkRequestBuilder<RemoveGlobalPlaylistItemWorker>()
            .setInputData(data)
            .build()

        WorkManager.getInstance(app)
            .enqueue(removeGlobalPlaylistItemWorkRequest)
    }

    override fun getAllObservable() = globalPlaylistRoomDataSource.getAllObservable()
    override suspend fun getAllOnce() = globalPlaylistRoomDataSource.getAllOnce()
    override suspend fun count() = globalPlaylistRoomDataSource.count()
    override fun removeItemAtPosition(position: Long) = globalPlaylistRoomDataSource.removeAtPosition(position)
}