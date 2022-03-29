package com.dimitrilc.freemediaplayer.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import com.dimitrilc.freemediaplayer.data.repos.MediaItemRepository
import com.dimitrilc.freemediaplayer.data.repos.MediaManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActivePlaylistViewModel @Inject constructor(
    mediaItemRepository: MediaItemRepository,
    private val mediaManager: MediaManager
) : ViewModel() {
    val activeMediaItemLiveData = mediaItemRepository.getActiveMediaItemObservable()

    suspend fun updateGlobalPlaylistAndActiveItem(playlist: List<MediaItem>, activeItem: MediaItem) {
        mediaManager.updateGlobalPlaylistAndActiveItem(playlist, activeItem)
    }
}