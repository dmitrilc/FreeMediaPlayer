package com.dimitrilc.freemediaplayer.ui.viewmodel.player

import androidx.lifecycle.*
import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import com.dimitrilc.freemediaplayer.domain.activemedia.*
import com.dimitrilc.freemediaplayer.domain.controls.*
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetActiveMediaItemObservableUseCase
import com.dimitrilc.freemediaplayer.ui.state.VideoPlayerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

private const val TAG = "VIDEO_PLAYER_VM"

//TOO many deps on contructor. Maybe code smell, but this is preferrable
// to field injection where fields cannot be private
@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val insertActiveMediaUseCase: InsertActiveMediaUseCase,
    private val updateMediaProgressUseCase: UpdateActiveMediaProgressUseCase,
    getActiveMediaObservableUseCase: GetActiveMediaObservableUseCase,
    private val updateActiveMediaPlaylistPositionAndMediaIdUseCase: UpdateActiveMediaPlaylistPositionAndMediaIdUseCase,
    private val skipToNextUseCase: SkipToNextUseCase,
    private val skipToPreviousUseCase: SkipToPreviousUseCase,
    private val playUseCase: PlayUseCase,
    private val pauseUseCase: PauseUseCase,
    private val shuffleUseCase: ShuffleUseCase,
    private val getActiveMediaItemObservableUseCase: GetActiveMediaItemObservableUseCase
): ViewModel() {

    lateinit var navigator: (()->Unit)

    private val _uiState = MutableLiveData<VideoPlayerUiState>(VideoPlayerUiState())
    val uiState: LiveData<VideoPlayerUiState> = _uiState.distinctUntilChanged()

    private val _actionFlow = MutableSharedFlow<Action>()

    private val _activeMediaItem = getActiveMediaItemObservableUseCase().asFlow()
    private val _activeMedia = getActiveMediaObservableUseCase()

    val accept: (Action) -> Unit = { action ->
        viewModelScope.launch {
            _actionFlow.emit(action)
        }
    }

    private var showControlJob: Job? = null

    init {
        viewModelScope.launch {
            _actionFlow.collect {
                val state: VideoPlayerUiState? = when(it){
                    is Action.SystemAction.EmitActiveMedia -> {
                        _uiState.value?.copy(
                            isPlaying = it.activeMedia.isPlaying,
                            repeatMode = it.activeMedia.repeatMode
                        )
                    }
                    is Action.SystemAction.EmitActiveMediaItem -> {
                        _uiState.value?.copy(
                            title = it.mediaItem.title,
                            album = it.mediaItem.album,
                            uri = it.mediaItem.uri,
                        )
                    }
                    is Action.UiAction.Playlist -> {
                        navigator.invoke()
                        null
                    }
                    is Action.UiAction.Play -> {
                        startHideControlsTimer()
                        _uiState.value?.copy(
                            isPlaying = true,
                            areControlsVisible = true
                        )
                    }
                    is Action.UiAction.Pause -> {
                        startHideControlsTimer()
                        _uiState.value?.copy(
                            isPlaying = false,
                            areControlsVisible = true
                        )
                    }
                    is Action.UiAction.SkipNext -> {
                        skipToNextUseCase()
                        _uiState.value?.copy(
                            position = 0,
                        )
                    }
                    is Action.UiAction.SkipPrevious -> {
                        skipToPreviousUseCase()
                        _uiState.value?.copy(
                            position = 0
                        )
                    }
                    is Action.UiAction.Shuffle -> {
                        startHideControlsTimer()
                        shuffleUseCase()
                        _uiState.value?.copy(
                            areControlsVisible = true
                        )
                    }
                    is Action.UiAction.SetRepeatMode -> {
                        startHideControlsTimer()
                        _uiState.value?.copy(
                            repeatMode = it.repeatMode,
                            areControlsVisible = true
                        )
                    }
                    is Action.UiAction.ShowControls -> {
                        startHideControlsTimer()
                        _uiState.value?.copy(
                            areControlsVisible = true
                        )
                    }
                    is Action.UiAction.HideControls -> {
                        _uiState.value?.copy(
                            areControlsVisible = false
                        )
                    }
                    is Action.UiAction.UpdateProgress -> {
                        _uiState.value?.let { state ->
                            val newPos = if (it.position > state.duration){
                                state.duration
                            } else if (it.position < 0){
                                0
                            } else {
                                it.position
                            }

                            _uiState.value?.copy(
                                position = newPos
                            )
                        }
                    }
                    is Action.UiAction.UpdateDuration -> {
                        _uiState.value?.copy(
                            duration = it.duration
                        )
                    }
                }

                state?.let {
                    _uiState.value = it
                }
            }
        }

        viewModelScope.launch {
            _activeMediaItem.filterNotNull().distinctUntilChanged().collect {
                accept(Action.SystemAction.EmitActiveMediaItem(it))
            }
        }

        viewModelScope.launch {
            _activeMedia.filterNotNull().distinctUntilChanged().collect {
                accept(Action.SystemAction.EmitActiveMedia(it))
            }
        }
    }

    private fun startHideControlsTimer() {
        showControlJob?.cancel()
        showControlJob = null

        showControlJob = viewModelScope.launch {
            delay(3000)
            if (isActive){
                accept(Action.UiAction.HideControls)
            }
        }
    }

    fun updateActiveMediaPlaylistPosition(position: Long) {
        updateActiveMediaPlaylistPositionAndMediaIdUseCase(position)
    }

}

sealed class Action {
    sealed class UiAction : Action() {
        object Playlist : UiAction()
        object Play : UiAction()
        object Pause : UiAction()
        object SkipNext: UiAction()
        object SkipPrevious : UiAction()
        object Shuffle : UiAction()
        data class SetRepeatMode(val repeatMode: Int) : UiAction()
        object ShowControls : UiAction()
        object HideControls : UiAction()
        data class UpdateProgress(val position: Int) : UiAction()
        data class UpdateDuration(val duration: Int) : UiAction()
    }
    sealed class SystemAction : Action() {
        data class EmitActiveMedia(val activeMedia: ActiveMedia) : SystemAction()
        data class EmitActiveMediaItem(val mediaItem: MediaItem) : SystemAction()
    }
}