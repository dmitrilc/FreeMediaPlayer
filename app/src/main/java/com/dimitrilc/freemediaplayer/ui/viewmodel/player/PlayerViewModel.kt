package com.dimitrilc.freemediaplayer.ui.viewmodel.player

import android.graphics.Bitmap
import androidx.lifecycle.*
import com.dimitrilc.freemediaplayer.domain.activemedia.GetActiveMediaObservableUseCase
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetActiveMediaItemObservableUseCase
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetMediaItemByIdUseCase
import com.dimitrilc.freemediaplayer.domain.mediastore.GetThumbByMediaIdUseCase
import com.dimitrilc.freemediaplayer.ui.state.PlayerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val getThumbByMediaIdUseCase: GetThumbByMediaIdUseCase,
    private val getActiveMediaObservableUseCase: GetActiveMediaObservableUseCase,
    private val getActiveMediaItemObservableUseCase: GetActiveMediaItemObservableUseCase
    ) : ViewModel() {

    private val _playerUiStateObservable = MutableLiveData<PlayerUiState>()
    val playerUiStateObservable: LiveData<PlayerUiState> = _playerUiStateObservable

    private val _thumbCache = mutableMapOf<Long, Bitmap?>()

    init {
        viewModelScope.launch {
            delay(300)
            getActiveMediaItemObservableUseCase()
                .asFlow()
                .filterNotNull()
                .combineTransform(getActiveMediaObservableUseCase().filterNotNull()){ mediaItem, activeMedia ->
                    /* Checks whether the local cache of thumbnail already
                    contains the thumbnail for this MediaItem */
                    val thumb = if (_thumbCache.containsKey(activeMedia.mediaItemId)){
                        _thumbCache[activeMedia.mediaItemId] //Gets thumbnail from cache
                    } else {
                        val result = getThumbByMediaIdUseCase(activeMedia.mediaItemId) //Loads new thumbnail
                        _thumbCache[activeMedia.mediaItemId] = result //Assigns new thumbnail to cache
                        result
                    }

                    emit(
                        PlayerUiState(
                            title = mediaItem.title,
                            album = mediaItem.album,
                            thumbnail = thumb,
                            position = activeMedia.progress,
                            duration = activeMedia.duration,
                            isPlaying = activeMedia.isPlaying,
                            repeatMode = activeMedia.repeatMode
                        )
                    )
                }.collect {
                    _playerUiStateObservable.postValue(it)
                }
        }
    }
}