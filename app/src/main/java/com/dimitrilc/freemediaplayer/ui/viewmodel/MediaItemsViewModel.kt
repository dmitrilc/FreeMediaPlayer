package com.dimitrilc.freemediaplayer.ui.viewmodel

import android.app.Application
import android.support.v4.media.MediaBrowserCompat
import androidx.lifecycle.*
import androidx.room.withTransaction
import com.dimitrilc.freemediaplayer.data.entities.ActiveMediaItem
import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import com.dimitrilc.freemediaplayer.data.repos.ActiveMediaRepository
import com.dimitrilc.freemediaplayer.data.repos.GlobalPlaylistRepository
import com.dimitrilc.freemediaplayer.data.repos.MediaItemRepository
import com.dimitrilc.freemediaplayer.data.room.database.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MEDIA_ITEMS_VIEW_MODEL"

@HiltViewModel
class MediaItemsViewModel @Inject constructor(
    private val appDb: AppDatabase,
    private val mediaItemRepository: MediaItemRepository
): ViewModel() {

    val audioBrowser = MutableLiveData<MediaBrowserCompat>()
    val globalPlaylist = appDb.globalPlaylistDao().getGlobalPlaylist()
    val activeMediaLiveData = appDb.activeMediaItemDao().getMediaItemLiveData()

/*    fun updateCurrentFolderFullPath(fullPath: String){
        viewModelScope.launch(Dispatchers.IO){
            appDb.folderItemsUiDao()
                .insertCurrentFolderItemsUi(
                    FolderItemsUiState(fullPath = fullPath)
                )
        }
    }*/

    fun updateGlobalPlaylistAndActiveItem(currentPath: String, selectedIndex: Int) {
/*        viewModelScope.launch(Dispatchers.IO){
            appDb.withTransaction {
                //Needs to run sequentially because of foreign key constraint
                val items = appDb.mediaItemDao().getAllAudioByLocation(currentPath)
                val playlist = items.mapIndexed { index, item ->
                    GlobalPlaylistItem(
                        mId = index.toLong(),
                        mediaItemId = item.id)
                }

                appDb.globalPlaylistDao().replacePlaylist(playlist)

                val selectedItem = items[selectedIndex]

                val activeItem = ActiveMediaItem(
                    globalPlaylistPosition = selectedIndex.toLong(),
                    mediaItemId = selectedItem.id
                )

                appDb.activeMediaItemDao().insert(activeItem)
            }
        }*/
        viewModelScope.launch(Dispatchers.IO){
            mediaItemRepository.updateGlobalPlaylistAndActiveItem(currentPath, selectedIndex)
        }
    }

    suspend fun insertActiveMedia(currentItemPos: Long, id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            appDb.activeMediaItemDao().insert(
                ActiveMediaItem(
                    globalPlaylistPosition = currentItemPos,
                    mediaItemId = id
                )
            )
        }
    }

    fun replacePlaylist(playlist: List<MediaItem>){
        viewModelScope.launch(Dispatchers.IO){
            val globalPlaylist = playlist.mapIndexed { index, item ->
                GlobalPlaylistItem(
                    index.toLong(), item.id
                )
            }

            appDb.globalPlaylistDao().replacePlaylist(
                globalPlaylist
            )
        }
    }

    suspend fun getActiveOnce() = appDb.activeMediaItemDao().getMediaItemOnce()
    suspend fun getPlaylistOnce() = appDb.globalPlaylistDao().getOnce()
}