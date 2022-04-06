package com.dimitrilc.freemediaplayer.ui.viewmodel

import androidx.lifecycle.*
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetAllMediaItemsObservableUseCase
import com.dimitrilc.freemediaplayer.ui.state.FoldersUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "FOLDERS_VIEW_MODEL"

@HiltViewModel
class FoldersViewModel @Inject constructor(
    private val getAllMediaItemsObservableUseCase: GetAllMediaItemsObservableUseCase
) : ViewModel() {

    private lateinit var _foldersUiState: LiveData<List<FoldersUiState>>

    val foldersUiStateMutableLiveData = MutableLiveData<List<FoldersUiState>>()

    fun start(isAudio: Boolean): LiveData<List<FoldersUiState>> {
        _foldersUiState = getAllMediaItemsObservableUseCase(isAudio).map { list ->
            list!!.distinctBy { it.location }
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

        viewModelScope.launch {
            _foldersUiState.asFlow().collect {
                foldersUiStateMutableLiveData.postValue(it)
            }
        }

        return _foldersUiState
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