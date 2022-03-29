package com.dimitrilc.freemediaplayer.data.repos

import androidx.room.withTransaction
import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import com.dimitrilc.freemediaplayer.data.room.database.AppDatabase
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
            val activeItem = ActiveMedia(
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

            val newActiveItem = ActiveMedia(
                globalPlaylistPosition = index.toLong(),
                mediaItemId = activeItem.id
            )

            activeMediaRepository.insert(newActiveItem)
        }
    }

    override suspend fun shuffleGlobalPlaylistAndActiveItem() {
        appDb.withTransaction {
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
        }
    }
}