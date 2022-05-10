package com.dimitrilc.freemediaplayer.ui.viewmodel.player

import android.util.Log
import androidx.lifecycle.*
import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaProgress
import com.dimitrilc.freemediaplayer.domain.activemedia.*
import com.dimitrilc.freemediaplayer.domain.controls.*
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetActiveMediaItemObservableUseCase
import com.dimitrilc.freemediaplayer.ui.state.VideoPlayerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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

    private val _uiState = MutableLiveData<VideoPlayerUiState>()
    val uiState: LiveData<VideoPlayerUiState> = _uiState.distinctUntilChanged()

    private val _activeMediaItem = MutableStateFlow<MediaItem?>(null)
    val activeMediaItem = _activeMediaItem.filterNotNull().distinctUntilChanged()

    private val _activeMedia = MutableStateFlow<ActiveMedia?>(null)
    val activeMedia: StateFlow<ActiveMedia?> = _activeMedia

    init {
        viewModelScope.launch {
            delay(300)
            getActiveMediaItemObservableUseCase()
                .asFlow()
                .combineTransform(getActiveMediaObservableUseCase()){ mediaItem, activeMedia ->
                    if (mediaItem != null && activeMedia != null) {
                        _activeMedia.value = activeMedia
                        _activeMediaItem.value = mediaItem

                        emit(
                            VideoPlayerUiState(
                                title = mediaItem.title,
                                album = mediaItem.album,
                                position = activeMedia.progress,
                                duration = activeMedia.duration,
                                isPlaying = activeMedia.isPlaying,
                                repeatMode = activeMedia.repeatMode
                            )
                        )
                    }
                }.collect {
                    Log.d(TAG, it.isPlaying.toString())
                    _uiState.postValue(it)
                }
        }
    }

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

    fun onProgressChanged(position: Long){
        viewModelScope.launch(Dispatchers.IO) {
            updateMediaProgressUseCase(ActiveMediaProgress(progress = position))
        }
    }

    fun onRepeatModeChanged(repeatMode: Int){
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
    }
}