package com.dimitrilc.freemediaplayer.ui.viewmodel

import android.support.v4.media.MediaBrowserCompat
import androidx.lifecycle.*
import com.dimitrilc.freemediaplayer.data.entities.ActiveMediaItem
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import com.dimitrilc.freemediaplayer.data.repos.ActiveMediaRepository
import com.dimitrilc.freemediaplayer.data.repos.GlobalPlaylistRepository
import com.dimitrilc.freemediaplayer.data.repos.MediaItemRepository
import com.dimitrilc.freemediaplayer.data.repos.MediaManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "MEDIA_ITEMS_VIEW_MODEL"

@HiltViewModel
class MediaItemsViewModel @Inject constructor(
    private val globalPlaylistRepository: GlobalPlaylistRepository,
    private val activeMediaRepository: ActiveMediaRepository,
    //private val mediaItemRepository: MediaItemRepository,
    private val mediaManager: MediaManager
): ViewModel() {
    val audioBrowser = MutableLiveData<MediaBrowserCompat>()
    val activeMediaItemLiveData = mediaManager.getActiveMediaItemObservable()

    suspend fun getActiveOnce() = mediaManager.getActiveMediaItemOnce()
    suspend fun getPlaylistOnce() = globalPlaylistRepository.getOnce()

    fun generateGlobalPlaylistAndActiveItem(currentPath: String, selectedIndex: Int, isAudio: Boolean) {
        viewModelScope.launch(Dispatchers.IO){
            mediaManager.generateGlobalPlaylistAndActiveItem(currentPath, selectedIndex, isAudio)
        }
    }

    suspend fun updateGlobalPlaylistAndActiveItem(playlist: List<MediaItem>, activeItem: MediaItem) {
        mediaManager.updateGlobalPlaylistAndActiveItem(playlist, activeItem)
    }

    fun insertActiveMedia(currentItemPos: Long, id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            activeMediaRepository.insert(
                ActiveMediaItem(
                globalPlaylistPosition = currentItemPos,
                mediaItemId = id
            ))
        }
    }

/*    fun replacePlaylist(playlist: List<MediaItem>){
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
    }*/
}