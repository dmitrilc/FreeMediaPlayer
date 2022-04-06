package com.dimitrilc.freemediaplayer.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetActiveMediaItemObservableUseCase
import com.dimitrilc.freemediaplayer.domain.UpdateGlobalPlaylistAndActiveMediaUseCase
import com.dimitrilc.freemediaplayer.domain.globalplaylist.RemoveGlobalPlaylistItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActivePlaylistViewModel @Inject constructor(
    getActiveMediaItemObservableUseCase: GetActiveMediaItemObservableUseCase,
    private val updateGlobalPlaylistAndActiveMediaUseCase: UpdateGlobalPlaylistAndActiveMediaUseCase,
    private val removeGlobalPlaylistItemUseCase: RemoveGlobalPlaylistItemUseCase
) : ViewModel() {
    val activeMediaItemLiveData = getActiveMediaItemObservableUseCase()

    fun updateGlobalPlaylistAndActiveMedia(playlist: List<MediaItem>, activeItem: MediaItem) {
        updateGlobalPlaylistAndActiveMediaUseCase(playlist, activeItem)
    }

    fun removeGlobalPlaylistItem(removed: GlobalPlaylistItem){
        removeGlobalPlaylistItemUseCase(removed)
    }
}