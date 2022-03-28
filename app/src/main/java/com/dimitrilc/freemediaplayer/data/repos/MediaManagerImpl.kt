package com.dimitrilc.freemediaplayer.data.repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.room.withTransaction
import com.dimitrilc.freemediaplayer.data.datasources.MediaItemRoomDataSource
import com.dimitrilc.freemediaplayer.data.entities.ActiveMediaItem
import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import com.dimitrilc.freemediaplayer.data.room.database.AppDatabase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class MediaManagerImpl @Inject constructor(
    private val mediaItemRepository: MediaItemRepository,
    private val globalPlaylistRepository: GlobalPlaylistRepository,
    private val activeMediaRepository: ActiveMediaRepository,
    private val appDb: AppDatabase
) : MediaManager {

    override suspend fun generateGlobalPlaylistAndActiveItem(currentPath: String, selectedIndex: Int, isAudio: Boolean) {
        appDb.withTransaction {
            val items = if (isAudio){
                mediaItemRepository.getAllAudioByLocation(currentPath)
            } else {
                mediaItemRepository.getAllVideoByLocation(currentPath)
            }

            val playlist = items.mapIndexed { index, item ->
                GlobalPlaylistItem(
                    mId = index.toLong(),
                    mediaItemId = item.id)
            }

            //Needs to run sequentially because of foreign key constraint
            globalPlaylistRepository.replacePlaylist(playlist)

            val selectedItem = items[selectedIndex]
            val activeItem = ActiveMediaItem(
                globalPlaylistPosition = selectedIndex.toLong(),
                mediaItemId = selectedItem.id
            )

            activeMediaRepository.insert(activeItem)
        }
    }

    override suspend fun updateGlobalPlaylistAndActiveItem(playlist: List<MediaItem>, activeItem: MediaItem) {
        appDb.withTransaction {
            val globalPlaylist = playlist.mapIndexed { index, item ->
                GlobalPlaylistItem(
                    mId = index.toLong(),
                    mediaItemId = item.id)
            }

            //Needs to run sequentially because of foreign key constraint
            globalPlaylistRepository.replacePlaylist(globalPlaylist)

            val index = playlist.indexOf(activeItem)

            val newActiveItem = ActiveMediaItem(
                globalPlaylistPosition = index.toLong(),
                mediaItemId = activeItem.id
            )

            activeMediaRepository.insert(newActiveItem)
        }
    }

    override suspend fun getActiveMediaItemOnce(): MediaItem {
        return appDb.withTransaction {
            val activeMedia = activeMediaRepository.getOnce()

            mediaItemRepository.getById(activeMedia.mediaItemId)
        }
    }

    override fun getActiveMediaItemObservable(): LiveData<MediaItem> {
        val flow = flow {
            activeMediaRepository.getObservable().collect {
                val mediaItem = mediaItemRepository.getById(it.mediaItemId)
                emit(mediaItem)
            }
        }

        return flow.asLiveData()
    }
}