package com.dimitrilc.freemediaplayer.ui.viewmodel

import androidx.lifecycle.*
import com.dimitrilc.freemediaplayer.domain.GetAllMediaItemsObservable
import com.dimitrilc.freemediaplayer.ui.state.FoldersUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "FOLDERS_VIEW_MODEL"

@HiltViewModel
class FoldersViewModel @Inject constructor(
    getAllMediaItemsObservable: GetAllMediaItemsObservable
    ) : ViewModel() {

    var isAudio = true

    private val _foldersUiState = getAllMediaItemsObservable(isAudio).map { list ->
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

    val foldersUiStateMutableLiveData = MutableLiveData<List<FoldersUiState>>()

    init {
        viewModelScope.launch {
            _foldersUiState.asFlow().collect {
                foldersUiStateMutableLiveData.postValue(it)
            }
        }
    }

    fun switchExpandedState(fullPathPos: Int){
        val newUiState = foldersUiStateMutableLiveData.value!!.asSequence()
            .mapIndexed{ idx, uiState ->
                if (idx == fullPathPos){
                    uiState.copy(isExpanded = !uiState.isExpanded)
                } else {
                    uiState
                }
            }.toList()

        foldersUiStateMutableLiveData.postValue(newUiState)
    }
}