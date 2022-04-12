package com.dimitrilc.freemediaplayer.ui.viewmodel

import androidx.lifecycle.*
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetMediaItemsByLocationUseCase
import com.dimitrilc.freemediaplayer.domain.mediastore.GetThumbByUriUseCase
import com.dimitrilc.freemediaplayer.domain.worker.GetUpdateActiveMediaWorkerInfoObservableUseCase
import com.dimitrilc.freemediaplayer.ui.fragments.folder.KEY_FULL_PATH
import com.dimitrilc.freemediaplayer.ui.state.FolderItemsUiState
import com.dimitrilc.freemediaplayer.ui.state.FolderItemsUiStateLight
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "FOLDER_ITEMS_VIEW_MODEL"

@HiltViewModel
class FolderItemsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    getMediaItemsByLocationUseCase: GetMediaItemsByLocationUseCase,
    private val getUpdateActiveMediaWorkerInfoObservableUseCase: GetUpdateActiveMediaWorkerInfoObservableUseCase,
    private val getThumbByUriUseCase: GetThumbByUriUseCase
    ) : ViewModel() {

    var isAudio = true
    var location = ""

    private val folderItemsUiStateLight by lazy {
        if (!savedStateHandle.contains("0")){
            getMediaItemsByLocationUseCase(isAudio, location).map { items ->
                items!!.map {
                    val videoId = if (isAudio) null else it.id

                    FolderItemsUiStateLight(
                        title = it.title,
                        album = it.album,
                        thumbnailUri = it.albumArtUri,
                        videoId = videoId
                    )
                }
            }
        } else {
            liveData {
                val value = savedStateHandle.keys().asSequence()
                    .filter { it != KEY_FULL_PATH }
                    .map { savedStateHandle.get<FolderItemsUiStateLight>(it) }
                    .filter { it != null }
                    .toList()

                emit(value as List<FolderItemsUiStateLight>)
            }
        }
    }

    val folderItemsUiState by lazy {
        folderItemsUiStateLight.map { list ->
            list.map {
                FolderItemsUiState(
                title = it.title,
                album = it.album,
                thumbnail = getThumbByUriUseCase(
                    it.thumbnailUri,
                    it.videoId
                ))
            }
        }
    }

    fun getUpdateActiveMediaWorkInfoObservable(uuid: String) =
        getUpdateActiveMediaWorkerInfoObservableUseCase(uuid)

    fun saveState() {
        folderItemsUiStateLight.value?.let { state ->
            state.forEachIndexed { index, it ->
                savedStateHandle["$index"] = it
            }
        }
    }
}