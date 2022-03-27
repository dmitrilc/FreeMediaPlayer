package com.dimitrilc.freemediaplayer.data.repos

import androidx.room.withTransaction
import com.dimitrilc.freemediaplayer.data.datasources.MediaItemRoomDataSource
import com.dimitrilc.freemediaplayer.data.entities.ActiveMediaItem
import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import com.dimitrilc.freemediaplayer.data.room.database.AppDatabase
import javax.inject.Inject

class MediaItemRepositoryImpl @Inject constructor(
    private val mediaItemRoomDataSource: MediaItemRoomDataSource,
    private val globalPlaylistRepository: GlobalPlaylistRepository,
    private val activeMediaRepository: ActiveMediaRepository,
    private val appDb: AppDatabase
    ) : MediaItemRepository {
    override suspend fun getAllAudio() = mediaItemRoomDataSource.getAllAudio()
    override suspend fun getAllVideo() = mediaItemRoomDataSource.getAllVideo()

    override suspend fun getAllAudioByLocation(location: String): List<MediaItem> = mediaItemRoomDataSource.getAllAudioByLocation(location)
    override suspend fun getAllVideoByLocation(location: String): List<MediaItem> = mediaItemRoomDataSource.getAllVideoByLocation(location)

    override fun getAllAudioObservable() = mediaItemRoomDataSource.getAllAudioObservable()
    override fun getAllVideoObservable() = mediaItemRoomDataSource.getAllVideoObservable()

    override suspend fun updateGlobalPlaylistAndActiveItem(currentPath: String, selectedIndex: Int) {
        appDb.withTransaction {
            val items = mediaItemRoomDataSource.getAllAudioByLocation(currentPath)
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
}