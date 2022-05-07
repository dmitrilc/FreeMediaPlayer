package com.dimitrilc.freemediaplayer.ui.viewmodel.player

import android.graphics.Bitmap
import androidx.lifecycle.*
import androidx.test.internal.util.Checks
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import com.dimitrilc.freemediaplayer.domain.activemedia.GetActiveMediaObservableUseCase
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetMediaItemByIdUseCase
import com.dimitrilc.freemediaplayer.domain.mediastore.GetThumbByMediaIdUseCase
import com.dimitrilc.freemediaplayer.ui.state.PlayerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val getThumbByMediaIdUseCase: GetThumbByMediaIdUseCase,
    private val getActiveMediaObservableUseCase: GetActiveMediaObservableUseCase,
    private val getMediaItemByIdUseCase: GetMediaItemByIdUseCase
    ) : ViewModel() {

    private val _playerUiStateObservable = MutableLiveData<PlayerUiState>()
    val playerUiStateObservable: LiveData<PlayerUiState> = _playerUiStateObservable

    private val _thumbCache = mutableMapOf<Long, Bitmap?>()
    private val _mediaItemCache = mutableMapOf<Long, MediaItem>()

    init {
        viewModelScope.launch {
            delay(300)
            getActiveMediaObservableUseCase().collect {
                if (it != null){
                    /* Checks whether the local cache of thumbnail already
                    contains the thumbnail for this MediaItem */
                    val thumb = if (_thumbCache.containsKey(it.mediaItemId)){
                        _thumbCache[it.mediaItemId] //Gets thumbnail from cache
                    } else {
                        val result = getThumbByMediaIdUseCase(it.mediaItemId) //Loads new thumbnail
                        _thumbCache[it.mediaItemId] = result //Assigns new thumbnail to cache
                        result
                    }

                    val mediaItem = if (_mediaItemCache.containsKey(it.mediaItemId)){
                        _mediaItemCache[it.mediaItemId]
                    } else {
                        getMediaItemByIdUseCase(it.mediaItemId)
                    }

                    if (mediaItem != null) {
                        _mediaItemCache[it.mediaItemId] = mediaItem
                    }

                    _playerUiStateObservable.postValue(
                        PlayerUiState(
                            title = mediaItem?.title,
                            album = mediaItem?.album,
                            thumbnail = thumb,
                            position = it.progress,
                            duration = it.duration,
                            isPlaying = it.isPlaying,
                            repeatMode = it.repeatMode
                        )
                    )
                }
            }
        }
    }
}