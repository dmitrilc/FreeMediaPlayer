package com.dimitrilc.freemediaplayer.ui.viewmodel.player

import android.provider.MediaStore
import android.util.Log
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

/*    private val _activeMediaItem = MutableStateFlow<MediaItem?>(null)
    val activeMediaItem = _activeMediaItem.filterNotNull().distinctUntilChanged()

    private val _activeMedia = MutableStateFlow<ActiveMedia?>(null)
    val activeMedia: StateFlow<ActiveMedia?> = _activeMedia*/

    private val _activeMediaItem = getActiveMediaItemObservableUseCase().asFlow()
    private val _activeMedia = getActiveMediaObservableUseCase()

    private val _progress = MutableStateFlow(0)
    private val progress: StateFlow<Int> = _progress

    val accept: (Action) -> Unit = { action ->
        viewModelScope.launch {
            _actionFlow.emit(action)
        }
    }

    private val _actionFlow = MutableSharedFlow<Action>()

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
                        _uiState.value?.copy(
                            isPlaying = true
                        )
                    }
                    is Action.UiAction.Pause -> {
                        _uiState.value?.copy(
                            isPlaying = false
                        )
                    }
                    is Action.UiAction.SkipNext -> {
                        skipToNextUseCase()
                        _uiState.value?.copy(
                            position = 0
                        )
                    }
                    is Action.UiAction.SkipPrevious -> {
                        skipToPreviousUseCase()
                        _uiState.value?.copy(
                            position = 0
                        )
                    }
                    is Action.UiAction.Shuffle -> {
                        shuffleUseCase()
                        null
                    }
                    is Action.UiAction.SetRepeatMode -> {
                        _uiState.value?.copy(
                            repeatMode = it.repeatMode
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

/*        viewModelScope.launch {
            _uiState.asFlow().collect {
                if (it.areControlsVisible){
                    Log.d(TAG, "Starting timer to hide controls")
                    startHideControlsTimer()
                }
            }
        }*/

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

/*        viewModelScope.launch {
            delay(300)
            getActiveMediaItemObservableUseCase()
                .asFlow()
                .combineTransform(getActiveMediaObservableUseCase()){ mediaItem, activeMedia ->
                    if (mediaItem != null && activeMedia != null) {
                        _activeMedia.value = activeMedia
                        _activeMediaItem.value = mediaItem

                        //Prevents Slider from throwing exception
                        val tmpDuration = if (activeMedia.duration == 0){
                            Long.MAX_VALUE
                        } else {
                            activeMedia.duration
                        }

                        emit(
                            VideoPlayerUiState(
                                title = mediaItem.title,
                                album = mediaItem.album,
                                duration = tmpDuration,
                                isPlaying = activeMedia.isPlaying,
                                repeatMode = activeMedia.repeatMode,
                                areControlsVisible = _uiState.value?.areControlsVisible ?: false
                            )
                        )
                    }
                }.collect {
                    _uiState.postValue(it)
                }
        }*/
    }

    private fun startHideControlsTimer(){
        cancelPreviousHideControlsTimer()

        //Log.d(TAG, "Starting timer to hide controls")

        showControlJob = viewModelScope.launch {
            delay(3000)
            if (isActive){
                _uiState.value = _uiState.value?.copy(areControlsVisible = false)
            }
        }
    }

    private fun cancelPreviousHideControlsTimer(){
        showControlJob?.cancel()
        showControlJob = null
    }

    fun hideControls(){
        _uiState.value = _uiState.value?.copy(areControlsVisible = false)
    }

    fun updateActiveMediaPlaylistPosition(position: Long) {
        updateActiveMediaPlaylistPositionAndMediaIdUseCase(position)
    }

/*    fun skipToNext() {
        startHideControlsTimer()
        onProgressChanged(0)
        skipToNextUseCase()
    }

    fun skipToPrevious() {
        startHideControlsTimer()
        onProgressChanged(0)
        skipToPreviousUseCase()
    }

    fun play(){
        startHideControlsTimer()
        viewModelScope.launch(Dispatchers.IO) {
            playUseCase()
        }
    }

    fun pause(){
        startHideControlsTimer()
        viewModelScope.launch(Dispatchers.IO) {
            pauseUseCase()
        }
    }

    fun shuffle() {
        startHideControlsTimer()
        shuffleUseCase()
    }

    fun onProgressChanged(position: Int){
        _activeMedia.value?.duration?.let { max ->
            if (position > max){
                _progress.value = max.toInt()
            } else if (position < 0){
                _progress.value = 0
            } else {
                _progress.value = position
            }
        }
    }

    fun onRepeatModeChanged(repeatMode: Int){
        startHideControlsTimer()
        _activeMedia.value?.copy(repeatMode = repeatMode)?.let {
            viewModelScope.launch(Dispatchers.IO) {
                insertActiveMediaUseCase(it)
            }
        }
    }

    fun onDurationChanged(duration: Long){
        _activeMedia.value?.copy(duration = duration)?.let {
            viewModelScope.launch(Dispatchers.IO) {
                insertActiveMediaUseCase(it)
            }
        }
    }*/
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