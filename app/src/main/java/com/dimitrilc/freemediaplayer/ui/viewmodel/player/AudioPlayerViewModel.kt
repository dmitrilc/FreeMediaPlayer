package com.dimitrilc.freemediaplayer.ui.viewmodel.player

import android.graphics.Bitmap
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import android.widget.SeekBar
import androidx.lifecycle.*
import com.dimitrilc.freemediaplayer.domain.mediastore.GetThumbByMediaIdUseCase
import com.dimitrilc.freemediaplayer.service.METADATA_KEY_ID
import com.dimitrilc.freemediaplayer.ui.state.AudioPlayerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AUDIO_PLAYER_VM"

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(
    private val getThumbByMediaIdUseCase: GetThumbByMediaIdUseCase
) : ViewModel(){

    var controller: MediaControllerCompat? = null

    var navigator: (()->Unit)? = null

    private val _uiState = MutableLiveData<AudioPlayerUiState>(AudioPlayerUiState())
    val uiState: LiveData<AudioPlayerUiState> = _uiState

    private val _actionFlow = MutableSharedFlow<AudioPlayerAction>()

    val accept: (AudioPlayerAction) -> Unit = { action ->
        viewModelScope.launch {
            _actionFlow.emit(action)
        }
    }

    private val _thumbCache = mutableMapOf<Long, Bitmap?>()

    //TODO Duplicate code
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
            _actionFlow.collect { action ->
                val state: AudioPlayerUiState? = when(action){
                    is AudioPlayerAction.ServiceAction.MetadataChanged -> {
                        action.metadata.let {
                            val id = it?.getLong(METADATA_KEY_ID)
                            val duration = it?.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
                            val title = it?.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
                            val album = it?.getString(MediaMetadataCompat.METADATA_KEY_ALBUM)

                            /* Checks whether the local cache of thumbnail already
                            contains the thumbnail for this MediaItem */
                            val thumb = if (_thumbCache.containsKey(id)){
                                _thumbCache[id] //Gets thumbnail from cache
                            } else {
                                val result = getThumbByMediaIdUseCase(id!!) //Loads new thumbnail
                                _thumbCache[id] = result //Assigns new thumbnail to cache
                                result
                            }

                            _uiState.value?.copy(
                                title = title,
                                album = album,
                                thumbnail = thumb,
                                duration = duration?.toInt() ?: 0,
                            )
                        }
                    }
                    is AudioPlayerAction.ServiceAction.PlaybackStateChanged -> {
                        //filter out type of state and emit actions into this same flow
                        action.state?.let {
                            when(action.state.state){
                                STATE_PLAYING -> {
                                    _uiState.value?.copy(
                                        isPlaying = true,
                                        position = it.position.toInt()
                                    )
                                }
                                STATE_PAUSED -> {
                                    _uiState.value?.copy(
                                        isPlaying = false,
                                        position = it.position.toInt()
                                    )
                                }
                                else -> { //Seeking
                                    _uiState.value?.copy(
                                        position = it.position.toInt()
                                    )
                                }
                            }
                        }
                    }
                    is AudioPlayerAction.ServiceAction.SetRepeatMode -> {
                        _uiState.value?.copy(
                            repeatMode = action.repeatMode
                        )
                    }
                    is AudioPlayerAction.UiAction.Playlist -> {
                        navigator?.invoke()
                        null
                    }
                }

                state?.let {
                    _uiState.value = it
                }
            }
        }
    }
}

sealed class AudioPlayerAction {
    sealed class UiAction : AudioPlayerAction() {
        object Playlist : UiAction()
    }
    sealed class ServiceAction : AudioPlayerAction() {
        data class SetRepeatMode(val repeatMode: Int) : AudioPlayerAction.UiAction()
        data class MetadataChanged(val metadata: MediaMetadataCompat?) : ServiceAction()
        data class PlaybackStateChanged(val state: PlaybackStateCompat?) : ServiceAction()
    }
}
//TODO GH ticket 59