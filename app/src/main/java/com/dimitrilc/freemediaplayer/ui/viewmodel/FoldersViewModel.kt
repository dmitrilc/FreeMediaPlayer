package com.dimitrilc.freemediaplayer.ui.viewmodel

import androidx.lifecycle.*
import com.dimitrilc.freemediaplayer.data.repos.MediaItemRepository
import com.dimitrilc.freemediaplayer.ui.state.FoldersUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "FOLDERS_VIEW_MODEL"

@HiltViewModel
class FoldersViewModel
@Inject constructor(
    private val mediaItemRepository: MediaItemRepository
    ) : ViewModel() {

    val foldersUiStateMutableCache = MutableLiveData<List<FoldersUiState>>()

    fun getImmutableFoldersUiState(isAudio: Boolean): LiveData<List<FoldersUiState>> {
        val items = if (isAudio){
            mediaItemRepository.getAllAudioObservable()
        } else {
            mediaItemRepository.getAllVideoObservable()
        }

        val result = items.map { list ->
            list.distinctBy { it.location }
            .groupBy({ it.location.substringBeforeLast('/') }) {
                it.location.substringAfterLast('/')
            }
            .map {
                FoldersUiState(
                    parentPath = it.key,
                    relativePaths = it.value
                )
            }
        }

        return result
    }

    fun onFolderFullClicked(fullPathPos: Int){
        val newUiState = foldersUiStateMutableCache.value!!.asSequence()
            .mapIndexed{ idx, uiState ->
                if (idx == fullPathPos){
                    uiState.copy(isExpanded = !uiState.isExpanded)
                } else {
                    uiState
                }
            }.toList()

        foldersUiStateMutableCache.postValue(newUiState)
    }
}