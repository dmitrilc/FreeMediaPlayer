package com.dimitrilc.freemediaplayer.ui.viewmodel.player

import android.graphics.Bitmap
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.SeekBar
import androidx.lifecycle.*
import com.dimitrilc.freemediaplayer.domain.activemedia.GetActiveMediaObservableUseCase
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetActiveMediaItemObservableUseCase
import com.dimitrilc.freemediaplayer.domain.mediastore.GetThumbByMediaIdUseCase
import com.dimitrilc.freemediaplayer.ui.state.PlayerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AUDIO_PLAYER_VM"

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    private val getThumbByMediaIdUseCase: GetThumbByMediaIdUseCase,
    private val getActiveMediaObservableUseCase: GetActiveMediaObservableUseCase,
    private val getActiveMediaItemObservableUseCase: GetActiveMediaItemObservableUseCase
) : ViewModel(){
    var controller: MediaControllerCompat? = null
    var navigateCallback: (() -> Unit)? = null

    private val _thumbCache = mutableMapOf<Long, Bitmap?>()

    private val _uiState = MutableLiveData<PlayerUiState>()
    val uiState: LiveData<PlayerUiState> = _uiState

    val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(
            seekBar: SeekBar?,
            progress: Int,
            fromUser: Boolean
        ) {
            if (fromUser) {
                controller?.transportControls?.seekTo(progress.toLong())
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

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
                            thumbnail = getThumbByMediaIdUseCase(mediaItem.mediaItemId),
                            position = activeMedia.progress,
                            duration = activeMedia.duration,
                            isPlaying = activeMedia.isPlaying,
                            repeatMode = activeMedia.repeatMode
                        )
                    )
                }.collect {
                    _uiState.postValue(it)
                }
        }
    }

    fun onPlayPauseClick(){
        if (_uiState.value?.isPlaying == true){
            controller?.transportControls?.pause()
        } else {
            controller?.transportControls?.play()
        }
    }

    //TOOD GH ticket 59
    fun onSeekNextClick() {
        controller?.transportControls?.skipToNext()
    }

    fun onSeekPreviousClick(){
        controller?.transportControls?.skipToPrevious()
    }

    fun onShuffleClick(){
        controller?.transportControls?.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL)
    }

    fun onReplayClick(){
        if (uiState.value?.repeatMode == PlaybackStateCompat.REPEAT_MODE_NONE){
            controller?.transportControls?.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE)
        } else {
            controller?.transportControls?.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE)
        }
    }

    fun onRewindClick(){
        controller?.transportControls?.rewind()
    }

    fun onForwardClick(){
        controller?.transportControls?.fastForward()
    }

    fun onPlaylistClick(){
        navigateCallback?.invoke()
    }

}