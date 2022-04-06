package com.dimitrilc.freemediaplayer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaProgress
import com.dimitrilc.freemediaplayer.domain.PlayCurrentUseCase
import com.dimitrilc.freemediaplayer.domain.activemedia.*
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetActiveMediaItemOnceUseCase
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetMediaItemsInGlobalPlaylistOnceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "VIDEO_PLAYER_VIEW_MODEL"

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val insertActiveMediaUseCase: InsertActiveMediaUseCase,
    private val updateMediaProgressUseCase: UpdateActiveMediaProgressUseCase,
    private val getActiveMediaItemOnceUseCase: GetActiveMediaItemOnceUseCase,
    private val getMediaItemsInGlobalPlaylistOnceUseCase: GetMediaItemsInGlobalPlaylistOnceUseCase,
    getActiveMediaObservableUseCase: GetActiveMediaObservableUseCase,
    private val updateActiveMediaPlaylistPositionUseCase: UpdateActiveMediaPlaylistPositionUseCase,
    private val skipToNextUseCase: SkipToNextUseCase,
    private val skipToPreviousUseCase: SkipToPreviousUseCase
): ViewModel() {
    var activeMediaCache: ActiveMedia? = null
    val activeMediaObservable = getActiveMediaObservableUseCase().asLiveData()

    fun postActiveMediaToRoom(activeMedia: ActiveMedia){
        viewModelScope.launch(Dispatchers.IO) {
            insertActiveMediaUseCase(activeMedia)
        }
    }

    suspend fun getActiveMediaItemOnce() = getActiveMediaItemOnceUseCase()

    suspend fun getMediaItemsInGlobalPlaylistOnce() = getMediaItemsInGlobalPlaylistOnceUseCase()

    fun updateActiveMediaProgress(progress: ActiveMediaProgress) {
        updateMediaProgressUseCase(progress)
    }

    fun updateActiveMediaPlaylistPosition(position: Long) {
        updateActiveMediaPlaylistPositionUseCase(position)
    }

    fun skipToNext() {
        skipToNextUseCase()
    }

    fun skipToPrevious() {
        skipToPreviousUseCase()
    }

}