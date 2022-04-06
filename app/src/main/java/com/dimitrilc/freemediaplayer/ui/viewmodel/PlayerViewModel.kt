package com.dimitrilc.freemediaplayer.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.*
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
                    val thumb = if (!_thumbCache.containsKey(it.mediaItemId)){
                        getThumbByMediaIdUseCase(it.mediaItemId)
                    } else {
                        _thumbCache[it.mediaItemId]
                    }

                    _thumbCache[it.mediaItemId] = thumb

                    val mediaItem = if (!_mediaItemCache.containsKey(it.mediaItemId)){
                        getMediaItemByIdUseCase(it.mediaItemId)
                    } else {
                        _mediaItemCache[it.mediaItemId]
                    }

                    if (mediaItem != null) {
                        _mediaItemCache[it.mediaItemId] = mediaItem
                    }

                    _playerUiStateObservable.postValue(
                        PlayerUiState(
                            title = mediaItem?.title ?: "null",
                            album = mediaItem?.album ?: "null",
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