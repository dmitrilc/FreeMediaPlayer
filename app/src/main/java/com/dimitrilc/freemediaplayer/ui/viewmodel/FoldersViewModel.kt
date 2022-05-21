package com.dimitrilc.freemediaplayer.ui.viewmodel

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.*
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetAllMediaItemsObservableUseCase
import com.dimitrilc.freemediaplayer.ui.state.callback.BiIntConsumer
import com.dimitrilc.freemediaplayer.ui.state.callback.IntConsumerCompat
import com.dimitrilc.freemediaplayer.ui.state.folders.FoldersFullUiState
import com.dimitrilc.freemediaplayer.ui.state.folders.ParcelableFoldersFullUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "FOLDERS_VIEW_MODEL"
const val KEY_FULL_PATH = "0"

@HiltViewModel
class FoldersViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getAllMediaItemsObservableUseCase: GetAllMediaItemsObservableUseCase
) : ViewModel() {
    lateinit var navigator: (String, Bundle)->Unit
    var isAudio = true

    private val _uiState = MutableStateFlow(listOf<FoldersFullUiState>())
    val uiState by lazy {
        loadUiState()
    }

    //Using one callback to save ram
    private val foldersFullClickListener = object: IntConsumerCompat {
        override fun invoke(bindingAdapterPos: Int) {
            val newUiState = _uiState.value
                .mapIndexed { index, foldersFullUiState ->
                    if (index == bindingAdapterPos){
                        foldersFullUiState.copy(isExpanded = !foldersFullUiState.isExpanded)
                    } else {
                        foldersFullUiState
                    }
                }

            viewModelScope.launch {
                _uiState.emit(newUiState)
            }
        }
    }

    //Using one callback to save ram
    private val foldersRelativeClickListener = object : BiIntConsumer{
        override fun invoke(fullPathPos: Int, bindingAdapterPos: Int) {
            val foldersUiState = _uiState.value[fullPathPos]
            val pathParent = foldersUiState.path
            val pathRelative = foldersUiState.relativePaths[bindingAdapterPos]
            val fullPath = "$pathParent/$pathRelative"

            val navArgs = bundleOf(KEY_FULL_PATH to fullPath)
            navigator(fullPath, navArgs)
        }
    }

    fun saveState(){
        _uiState.value.forEachIndexed { idx, state ->
            savedStateHandle["$idx"] = state.toParcelable()
        }
    }

    private fun loadUiState(): StateFlow<List<FoldersFullUiState>> {
        if (savedStateHandle.keys().isNotEmpty()){
            initFromSavedState()
        } else {
            initFoldersUiStateFromUseCase()
        }

        return _uiState.asStateFlow()
    }

    private fun initFoldersUiStateFromUseCase(){
        val result = getAllMediaItemsObservableUseCase(isAudio).map { list ->
            list?.asSequence()
                ?.distinctBy {
                    it.location
                }
                ?.groupBy({
                    it.location.substringBeforeLast('/')
                }) {
                    it.location.substringAfterLast('/')
                }
                ?.map {
                    FoldersFullUiState(
                        path = it.key,
                        relativePaths = it.value,
                        onFullClick = foldersFullClickListener,
                        onRelativeClick = foldersRelativeClickListener
                    )
                }
        }
            .asFlow()
            .filterNotNull()

        viewModelScope.launch {
            result.collect {
                _uiState.emit(it)
            }
        }
    }

    private fun initFromSavedState() {
        viewModelScope.launch {
            val state = savedStateHandle.keys()
                .mapNotNull {
                    savedStateHandle
                        .get<ParcelableFoldersFullUiState>(it)
                        ?.toState(
                            foldersFullClickListener,
                            foldersRelativeClickListener
                        )
                }

            _uiState.emit(state)
        }
    }
}