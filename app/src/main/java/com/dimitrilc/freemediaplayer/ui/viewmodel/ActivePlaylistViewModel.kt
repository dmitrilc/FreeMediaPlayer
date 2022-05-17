package com.dimitrilc.freemediaplayer.ui.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.*
import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetActiveMediaItemObservableUseCase
import com.dimitrilc.freemediaplayer.domain.UpdateGlobalPlaylistAndActiveMediaUseCase
import com.dimitrilc.freemediaplayer.domain.activemedia.InsertActiveMediaUseCase
import com.dimitrilc.freemediaplayer.domain.activemedia.UpdateActiveMediaByGlobalPlaylistPositionUseCase
import com.dimitrilc.freemediaplayer.domain.activemedia.UpdateActiveMediaByObjectUseCase
import com.dimitrilc.freemediaplayer.domain.globalplaylist.RemoveGlobalPlaylistItemUseCase
import com.dimitrilc.freemediaplayer.domain.globalplaylist.MoveGlobalPlaylistItemPositionUseCase
import com.dimitrilc.freemediaplayer.domain.globalplaylist.SwipedUseCase
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetMediaItemsInGlobalPlaylistObservableUseCase
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetMediaItemsInGlobalPlaylistOnceUseCase
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
    private val moveGlobalPlaylistItemPositionUseCase: MoveGlobalPlaylistItemPositionUseCase,
    private val swipedUseCase: SwipedUseCase
) : ViewModel() {

    private val onClick = object : IntConsumerCompat {
        override fun invoke(pos: Int) {
            Log.d(TAG, "$pos")
            //TODO use Worker
            viewModelScope.launch(Dispatchers.IO) {
                insertActiveMediaUseCase(
                    ActiveMedia(
                        globalPlaylistPosition = pos.toLong(),
                        mediaItemId = _playlist.value[pos].mediaItemId
                    )
                )
            }
        }
    }

    private val _playlist = MutableStateFlow<List<MediaItem>>(listOf())
    val playlist = _playlist.asStateFlow()

    val cache = mutableListOf<FolderItemsUiState>()

/*        _playlistCache.map {
        FolderItemsUiState(
            title = it.title,
            album = it.album,
            thumbnailUri = it.albumArtUri,
            videoId = if (it.isAudio) null else it.mediaItemId,
            thumbnailLoader = thumbLoader,
            onClick = onClick
        )
    }*/

/*    private val _mediaItemsInGlobalPlaylist = MutableStateFlow<List<MediaItem>>(listOf())

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
    }*/

    init {
        viewModelScope.launch {
            getMediaItemsInGlobalPlaylistObservableUseCase()
                .asFlow()
                .filterNotNull()
                .collect { playlist ->
                    if (cache.isEmpty()){
                        val transformed = playlist.map {
                            FolderItemsUiState(
                                title = it.title,
                                album = it.album,
                                thumbnailUri = it.albumArtUri,
                                videoId = if (it.isAudio) null else it.mediaItemId,
                                thumbnailLoader = thumbLoader,
                                onClick = onClick
                            )
                        }

                        cache.addAll(transformed)
                    }

                    _playlist.value = playlist
                }
        }

/*        viewModelScope.launch {
            getMediaItemsInGlobalPlaylistObservableUseCase()?.let { playlist ->
                _playlistCache.addAll(playlist)
                playlistCache.addAll(playlist.map {
                    FolderItemsUiState(
                        title = it.title,
                        album = it.album,
                        thumbnailUri = it.albumArtUri,
                        videoId = if (it.isAudio) null else it.mediaItemId,
                        thumbnailLoader = thumbLoader,
                        onClick = onClick
                    )
                })
            }
        }*/
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

    fun onPlaylistItemMoved(from: Int, to: Int) {
        val movedItem = cache.removeAt(from)
        cache.add(to, movedItem)

        moveGlobalPlaylistItemPositionUseCase(from, to)
    }

    fun onPlaylistItemRemoved(pos: Int) {
        swipedUseCase(pos.toLong())
    }

}