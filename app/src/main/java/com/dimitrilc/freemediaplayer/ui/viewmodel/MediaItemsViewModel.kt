package com.dimitrilc.freemediaplayer.ui.viewmodel

import android.support.v4.media.MediaBrowserCompat
import androidx.lifecycle.*
import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import com.dimitrilc.freemediaplayer.data.repos.ActiveMediaRepository
import com.dimitrilc.freemediaplayer.data.repos.MediaItemRepository
import com.dimitrilc.freemediaplayer.data.repos.MediaManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MEDIA_ITEMS_VIEW_MODEL"

@HiltViewModel
class MediaItemsViewModel @Inject constructor(
    private val activeMediaRepository: ActiveMediaRepository,
    private val mediaItemRepository: MediaItemRepository,
    private val mediaManager: MediaManager
): ViewModel() {
    val audioBrowser = MutableLiveData<MediaBrowserCompat>()

    suspend fun getActiveMediaItemOnce() = mediaItemRepository.getActiveMediaItemOnce()
    suspend fun getPlaylistOnce() = mediaItemRepository.getMediaItemsInGlobalPlaylistOnce()

    fun generateGlobalPlaylistAndActiveItem(currentPath: String, selectedIndex: Int, isAudio: Boolean) {
        viewModelScope.launch(Dispatchers.IO){
            mediaManager.generateGlobalPlaylistAndActiveItem(currentPath, selectedIndex, isAudio)
        }
    }

    fun insertActiveMedia(currentItemPos: Long, id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            activeMediaRepository.insert(
                ActiveMedia(
                globalPlaylistPosition = currentItemPos,
                mediaItemId = id
            ))
        }
    }
}