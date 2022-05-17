package com.dimitrilc.freemediaplayer.ui.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.*
import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import com.dimitrilc.freemediaplayer.domain.activemedia.InsertActiveMediaUseCase
import com.dimitrilc.freemediaplayer.domain.globalplaylist.MoveGlobalPlaylistItemPositionUseCase
import com.dimitrilc.freemediaplayer.domain.globalplaylist.SwipedUseCase
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetMediaItemsInGlobalPlaylistObservableUseCase
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
    private val getThumbByUriUseCase: GetThumbByUriUseCase,
    private val getMediaItemsInGlobalPlaylistObservableUseCase: GetMediaItemsInGlobalPlaylistObservableUseCase,
    private val insertActiveMediaUseCase: InsertActiveMediaUseCase,
    private val moveGlobalPlaylistItemPositionUseCase: MoveGlobalPlaylistItemPositionUseCase,
    private val swipedUseCase: SwipedUseCase
) : ViewModel() {

    private val _playlist = MutableStateFlow<List<MediaItem>>(listOf())
    val playlist = _playlist.asStateFlow()

    val cache = mutableListOf<FolderItemsUiState>()

    private val onClick = object : IntConsumerCompat {
        override fun invoke(pos: Int) {
            Log.d(TAG, pos.toString())
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

    }

    private val thumbLoader = object : CustomBiFunction<String?, Long?, Bitmap?> {
        override fun invoke(thumbUri: String?, videoId: Long?): Bitmap? {
            return getThumbByUriUseCase(thumbUri, videoId)
        }
    }

    fun onPlaylistItemMoved(from: Int, to: Int) {
        val movedItem = cache.removeAt(from)
        cache.add(to, movedItem)

        moveGlobalPlaylistItemPositionUseCase(from, to)
    }

    fun onPlaylistItemRemoved(pos: Int) {
        cache.removeAt(pos)
        swipedUseCase(pos.toLong())
    }

}