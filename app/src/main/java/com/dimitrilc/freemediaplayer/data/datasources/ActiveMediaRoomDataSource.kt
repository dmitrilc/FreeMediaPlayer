package com.dimitrilc.freemediaplayer.data.datasources

import androidx.lifecycle.LiveData
import com.dimitrilc.freemediaplayer.data.entities.ActiveMediaItem
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import kotlinx.coroutines.flow.Flow

interface ActiveMediaRoomDataSource {
    fun insert(activeMediaItem: ActiveMediaItem)

    suspend fun getOnce(): ActiveMediaItem

/*    suspend fun getActiveMediaItemOnce(): MediaItem

    fun getMediaItemObservable(): LiveData<MediaItem>*/

    fun getObservable(): Flow<ActiveMediaItem>
}