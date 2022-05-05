package com.dimitrilc.freemediaplayer.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.*
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetMediaItemsByLocationUseCase
import com.dimitrilc.freemediaplayer.domain.mediastore.GetThumbByUriUseCase
import com.dimitrilc.freemediaplayer.domain.worker.GetUpdateActiveMediaWorkerInfoObservableUseCase
import com.dimitrilc.freemediaplayer.ui.state.folders.items.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "FOLDER_ITEMS_VIEW_MODEL"

@HiltViewModel
class FolderItemsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getMediaItemsByLocationUseCase: GetMediaItemsByLocationUseCase,
    private val getUpdateActiveMediaWorkerInfoObservableUseCase: GetUpdateActiveMediaWorkerInfoObservableUseCase,
    private val getThumbByUriUseCase: GetThumbByUriUseCase
    ) : ViewModel() {

    var isAudio = true
    var location = ""
    lateinit var navigator: CustomIntConsumer

    private val onClick = object : CustomIntConsumer {
        override fun invoke(pos: Int) {
            navigator(pos)
        }
    }

    private val thumbLoader = object : CustomBiFunction<String?, Long?, Bitmap?> {
        override fun invoke(thumbUri: String?, videoId: Long?): Bitmap? {
            return getThumbByUriUseCase(thumbUri, videoId)
        }
    }

    private val _uiState = MutableStateFlow<List<FolderItemsUiState>>(listOf())
    val uiState by lazy {
        loadUiState()
    }

    private fun loadUiState(): StateFlow<List<FolderItemsUiState>> {
        //The bundle passed to the fragment automatically adds the item at index zero
        if (savedStateHandle.keys().size > 1){
            initFromSavedState()
        } else {
            initFromUseCase()
        }

        return _uiState.asStateFlow()
    }

    private fun initFromSavedState() {
        val value = savedStateHandle.keys().asSequence()
            .filter { it != KEY_FULL_PATH }
            .map {
                val tmp = savedStateHandle.get<ParcelableFolderItemsUiState>(it)
                tmp?.toState(
                    thumbLoader,
                    onClick
                )
            }
            .filterNotNull()
            .toList()

        viewModelScope.launch {
            _uiState.emit(value)
        }
    }

    private fun initFromUseCase(){
        val result = getMediaItemsByLocationUseCase(isAudio, location).map { items ->
            items?.map {
                val videoId = if (isAudio) null else it.id

                FolderItemsUiState(
                    title = it.title,
                    album = it.album,
                    thumbnailUri = it.albumArtUri,
                    videoId = videoId,
                    thumbnailLoader = thumbLoader,
                    onClick = onClick
                )
            }
        }
            .asFlow()
            .filterNotNull()

        viewModelScope.launch {
            result.collect{
                _uiState.emit(it)
            }
        }
    }

    fun getUpdateActiveMediaWorkInfoObservable(uuid: String) =
        getUpdateActiveMediaWorkerInfoObservableUseCase(uuid)

    fun saveState() {
        _uiState.value.let { state ->
            state.map {
                it.toParcel()
            }.forEach {
                savedStateHandle[it.title] = it
            }
        }
    }
}