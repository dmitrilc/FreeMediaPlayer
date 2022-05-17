package com.dimitrilc.freemediaplayer.data.repos.globalplaylist

import android.app.Application
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.dimitrilc.freemediaplayer.data.source.room.globalplaylist.GlobalPlaylistLocalDataSource
import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem
import com.dimitrilc.freemediaplayer.data.worker.*
import com.dimitrilc.freemediaplayer.data.worker.globalplaylist.RemoveGlobalPlaylistItemWorker
import javax.inject.Inject

class GlobalPlaylistRepositoryImpl
@Inject constructor(
    private val globalPlaylistLocalDataSource: GlobalPlaylistLocalDataSource,
    private val app: Application
    )
    : GlobalPlaylistRepository {
    override fun replace(playlist: List<GlobalPlaylistItem>) =
        globalPlaylistLocalDataSource.replace(playlist)

    override fun removeItem(item: GlobalPlaylistItem) {
        val data = Data.Builder()
            .putLong(WORKER_DATA_KEY_GLOBAL_PLAYLIST_INDEX, item.globalPlaylistItemId)
            .putLong(WORKER_DATA_KEY_MEDIA_ITEM_ID, item.mediaItemId)
            .build()

        val removeGlobalPlaylistItemWorkRequest = OneTimeWorkRequestBuilder<RemoveGlobalPlaylistItemWorker>()
            .setInputData(data)
            .build()

        WorkManager.getInstance(app)
            .enqueue(removeGlobalPlaylistItemWorkRequest)
    }

    override fun getAllObservable() = globalPlaylistLocalDataSource.getAllObservable()
    override suspend fun getAllOnce() = globalPlaylistLocalDataSource.getAllOnce()
    override suspend fun count() = globalPlaylistLocalDataSource.count()
    override fun removeItemAtPosition(position: Long) = globalPlaylistLocalDataSource.removeAtPosition(position)
    override suspend fun getByPosition(pos: Int) = globalPlaylistLocalDataSource.getByPosition(pos)
    override fun delete(vararg items: GlobalPlaylistItem) = globalPlaylistLocalDataSource.delete(*items)
}