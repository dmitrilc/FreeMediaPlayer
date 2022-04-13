package com.dimitrilc.freemediaplayer.ui.viewmodel

import androidx.core.os.bundleOf
import androidx.databinding.BindingAdapter
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetAllMediaItemsObservableUseCase
import com.dimitrilc.freemediaplayer.ui.adapter.FoldersFullAdapter
import com.dimitrilc.freemediaplayer.ui.fragments.folder.KEY_FULL_PATH
import com.dimitrilc.freemediaplayer.ui.state.FoldersUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "FOLDERS_VIEW_MODEL"

@HiltViewModel
class FoldersViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getAllMediaItemsObservableUseCase: GetAllMediaItemsObservableUseCase
) : ViewModel() {

    private lateinit var _foldersUiState: LiveData<List<FoldersUiState>>
    val foldersUiStateMutableLiveData = MutableLiveData<List<FoldersUiState>>()

    fun saveState(){
        foldersUiStateMutableLiveData.value?.let { state ->
            state.forEach {
                savedStateHandle[it.parentPath] = it
            }
        }
    }

    fun getFoldersUiStateMutable(isAudio: Boolean): MutableLiveData<List<FoldersUiState>> {
        if (savedStateHandle.keys().isEmpty()){
            initFoldersUiStateFromUseCase(isAudio)
        } else {
            initFoldersUiStateFromHandle()
        }

        return foldersUiStateMutableLiveData
    }

    private fun initFoldersUiStateFromUseCase(isAudio: Boolean){
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

        collectToFoldersUiStateCache()
    }

    private fun collectToFoldersUiStateCache(){
        viewModelScope.launch {
            _foldersUiState.asFlow().collect {
                foldersUiStateMutableLiveData.postValue(it)
            }
        }
    }

    private fun initFoldersUiStateFromHandle(){
        val state = savedStateHandle.keys().asSequence()
            .map { savedStateHandle.get<FoldersUiState>(it) }
            .filter { it != null }
            .toList()

        if (state.isNotEmpty()){
            foldersUiStateMutableLiveData.postValue(state as List<FoldersUiState>)
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