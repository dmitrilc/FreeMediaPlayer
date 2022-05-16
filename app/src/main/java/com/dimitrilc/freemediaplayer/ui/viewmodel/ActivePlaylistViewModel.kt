package com.dimitrilc.freemediaplayer.ui.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.*
import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetActiveMediaItemObservableUseCase
import com.dimitrilc.freemediaplayer.domain.UpdateGlobalPlaylistAndActiveMediaUseCase
import com.dimitrilc.freemediaplayer.domain.activemedia.InsertActiveMediaUseCase
import com.dimitrilc.freemediaplayer.domain.activemedia.UpdateActiveMediaByGlobalPlaylistPositionUseCase
import com.dimitrilc.freemediaplayer.domain.activemedia.UpdateActiveMediaByObjectUseCase
import com.dimitrilc.freemediaplayer.domain.globalplaylist.RemoveGlobalPlaylistItemUseCase
import com.dimitrilc.freemediaplayer.domain.globalplaylist.MoveGlobalPlaylistItemPositionUseCase
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetMediaItemsInGlobalPlaylistObservableUseCase
import com.dimitrilc.freemediaplayer.domain.mediastore.GetThumbByMediaIdUseCase
import com.dimitrilc.freemediaplayer.domain.mediastore.GetThumbByUriUseCase
import com.dimitrilc.freemediaplayer.ui.state.callback.CustomBiFunction
import com.dimitrilc.freemediaplayer.ui.state.callback.IntConsumerCompat
import com.dimitrilc.freemediaplayer.ui.state.folders.items.FolderItemsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ACTIVE_PLAYLIST_VM"

@HiltViewModel
class ActivePlaylistViewModel @Inject constructor(
    getActiveMediaItemObservableUseCase: GetActiveMediaItemObservableUseCase,
    private val updateGlobalPlaylistAndActiveMediaUseCase: UpdateGlobalPlaylistAndActiveMediaUseCase,
    private val removeGlobalPlaylistItemUseCase: RemoveGlobalPlaylistItemUseCase,
    private val getThumbByUriUseCase: GetThumbByUriUseCase,
    private val getMediaItemsInGlobalPlaylistObservableUseCase: GetMediaItemsInGlobalPlaylistObservableUseCase,
    private val getThumbByMediaIdUseCase: GetThumbByMediaIdUseCase,
    private val updateActiveMediaByGlobalPlaylistPositionUseCase: UpdateActiveMediaByGlobalPlaylistPositionUseCase,
    private val updateActiveMediaByObjectUseCase: UpdateActiveMediaByObjectUseCase,
    private val insertActiveMediaUseCase: InsertActiveMediaUseCase,
    private val moveGlobalPlaylistItemPositionUseCase: MoveGlobalPlaylistItemPositionUseCase
) : ViewModel() {
    val activeMediaItemLiveData = getActiveMediaItemObservableUseCase()

    private val onClick = object : IntConsumerCompat {
        override fun invoke(pos: Int) {
            Log.d(TAG, "Inserting active media")
            //TODO use Worker
            _mediaItemsInGlobalPlaylist.value.let {
                viewModelScope.launch(Dispatchers.IO) {
                    insertActiveMediaUseCase(
                        ActiveMedia(
                            globalPlaylistPosition = pos.toLong(),
                            mediaItemId = it[pos].mediaItemId
                        )
                    )
                }
            }
        }
    }

    private val _mediaItemsInGlobalPlaylist = MutableStateFlow<List<MediaItem>>(listOf())

/*    private val _uiState = MutableLiveData<List<FolderItemsUiState>>()
    val uiState: LiveData<List<FolderItemsUiState>> = _uiState*/
    val uiState = _mediaItemsInGlobalPlaylist.map { items ->
        items.map {
            FolderItemsUiState(
                title = it.title,
                album = it.album,
                thumbnailUri = it.albumArtUri,
                videoId = if (it.isAudio) null else it.mediaItemId,
                thumbnailLoader = thumbLoader,
                onClick = onClick
            )
        }
    }

    init {
        viewModelScope.launch {
            getMediaItemsInGlobalPlaylistObservableUseCase()
                .asFlow()
                .filterNotNull()
                .collect {
                    _mediaItemsInGlobalPlaylist.value = it
                }
        }
    }

/*    private val thumbLoader = object : CustomBiFunction<String?, Long?, Bitmap?> {
        override fun invoke(thumbUri: String?, videoId: Long?): Bitmap? {
            return getThumbByUriUseCase(thumbUri, videoId)
        }
    }*/

    private val thumbLoader = object : CustomBiFunction<String?, Long?, Bitmap?> {
        override fun invoke(thumbUri: String?, videoId: Long?): Bitmap? {
            return getThumbByUriUseCase(thumbUri, videoId)
        }
    }

    init {
/*        viewModelScope.launch {
            _mediaItemsInGlobalPlaylist.asFlow().collect { items ->
                items?.map {
                    FolderItemsUiState(
                        title = it.title,
                        album = it.album,
                        thumbnailUri = it.albumArtUri,
                        videoId = if (it.isAudio) null else it.mediaItemId,
                        thumbnailLoader = thumbLoader,
                        onClick = onClick
                    )
                }
            }
        }*/
    }

    fun updateGlobalPlaylistAndActiveMedia(playlist: List<MediaItem>, activeItem: MediaItem) {
        updateGlobalPlaylistAndActiveMediaUseCase(playlist, activeItem)
    }
    
    fun removeGlobalPlaylistItemAtPosition(pos: Int){
        
    }

    fun removeGlobalPlaylistItem(removed: GlobalPlaylistItem){
        removeGlobalPlaylistItemUseCase(removed)
    }

    //TOOD Clean up
    fun setActiveMedia(bindingAdapterPosition: Int){
/*        val controller: MediaController? = requireActivity().mediaController

        if (controller != null){ //audio
            activePlaylistViewModel.updateGlobalPlaylistAndActiveMedia(mPlaylistCache, mPlaylistCache[bindingAdapterPosition])
            //controller.transportControls.skipToQueueItem(bindingAdapterPosition.toLong())
        } else { //video
            activePlaylistViewModel.updateGlobalPlaylistAndActiveMedia(mPlaylistCache, mPlaylistCache[bindingAdapterPosition])
            val navController = findNavController()
            navController.popBackStack()
        }*/
    }

    fun moveGlobalPlaylistItemsPositions(from: Int, to: Int) {
        moveGlobalPlaylistItemPositionUseCase(from, to)
    }

}