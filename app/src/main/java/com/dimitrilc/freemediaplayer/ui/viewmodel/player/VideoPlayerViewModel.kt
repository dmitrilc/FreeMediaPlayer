package com.dimitrilc.freemediaplayer.ui.viewmodel.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import com.dimitrilc.freemediaplayer.domain.activemedia.*
import com.dimitrilc.freemediaplayer.domain.controls.*
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetActiveMediaItemOnceUseCase
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetMediaItemsInGlobalPlaylistOnceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "VIDEO_PLAYER_VIEW_MODEL"


//TOO many deps on contructor. Maybe code smell, but this is preferrable
// to field injection where fields cannot be private
@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val insertActiveMediaUseCase: InsertActiveMediaUseCase,
    private val updateMediaProgressUseCase: UpdateActiveMediaProgressUseCase,
    private val getActiveMediaItemOnceUseCase: GetActiveMediaItemOnceUseCase,
    private val getMediaItemsInGlobalPlaylistOnceUseCase: GetMediaItemsInGlobalPlaylistOnceUseCase,
    getActiveMediaObservableUseCase: GetActiveMediaObservableUseCase,
    private val updateActiveMediaPlaylistPositionAndMediaIdUseCase: UpdateActiveMediaPlaylistPositionAndMediaIdUseCase,
    private val skipToNextUseCase: SkipToNextUseCase,
    private val skipToPreviousUseCase: SkipToPreviousUseCase,
    private val playUseCase: PlayUseCase,
    private val pauseUseCase: PauseUseCase,
    private val shuffleUseCase: ShuffleUseCase
): ViewModel() {
    var activeMediaCache: ActiveMedia? = null
    var activeMediaItemCache: MediaItem? = null
    val activeMediaObservable = getActiveMediaObservableUseCase().asLiveData()

    fun postActiveMediaToRoom(activeMedia: ActiveMedia){
        viewModelScope.launch(Dispatchers.IO) {
            insertActiveMediaUseCase(activeMedia)
        }
    }

    suspend fun getActiveMediaItemOnce() = getActiveMediaItemOnceUseCase()

    suspend fun getMediaItemsInGlobalPlaylistOnce() = getMediaItemsInGlobalPlaylistOnceUseCase()

    fun updateActiveMediaPlaylistPosition(position: Long) {
        updateActiveMediaPlaylistPositionAndMediaIdUseCase(position)
    }

    fun skipToNext() {
        skipToNextUseCase()
    }

    fun skipToPrevious() {
        skipToPreviousUseCase()
    }

    fun play(){
        viewModelScope.launch(Dispatchers.IO) {
            playUseCase()
        }
    }

    fun pause(){
        viewModelScope.launch(Dispatchers.IO) {
            pauseUseCase()
        }
    }

    fun shuffle() {
        shuffleUseCase()
    }

}