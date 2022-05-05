package com.dimitrilc.freemediaplayer.ui.viewmodel

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.*
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetAllMediaItemsObservableUseCase
import com.dimitrilc.freemediaplayer.ui.state.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "FOLDERS_VIEW_MODEL"
private const val FOLDERS_UI_STATE_KEY = "0"
private const val KEY_FULL_PATH = "0"

@HiltViewModel
class FoldersViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getAllMediaItemsObservableUseCase: GetAllMediaItemsObservableUseCase
) : ViewModel() {
    lateinit var navigator: (String, Bundle)->Unit
    var isAudio: Boolean = false

    private val _uiState = MutableStateFlow(FoldersUiState(listOf()))
    val uiState by lazy {
        loadUiState()
    }

    //Using one callback to save ram
    private val foldersFullClickListener: (Int)->Unit = {
        val newUiState = _uiState.value.fullFolders
            .asSequence()
            .mapIndexed { index, foldersFullUiState ->
                if (index == it){
                    foldersFullUiState.copy(isExpanded = !foldersFullUiState.isExpanded)
                } else {
                    foldersFullUiState
                }
            }.map {
                FoldersUiState(fullFolders = listOf(it))
            }.reduce { acc, foldersUiState ->
                acc.copy(
                    fullFolders = acc.fullFolders.plus(foldersUiState.fullFolders)
                )
            }

        viewModelScope.launch {
            _uiState.emit(newUiState)
        }
    }

    //Using one callback to save ram
    private val foldersRelativeClickListener: (Int,Int)->Unit = { fullPathPos, bindingAdapterPos ->
        val foldersUiState = _uiState.value.fullFolders[fullPathPos]
        val pathParent = foldersUiState.path
        val pathRelative = foldersUiState.relativePath.path[bindingAdapterPos]
        val fullPath = "$pathParent/$pathRelative"

        val navArgs = bundleOf(KEY_FULL_PATH to fullPath)
        navigator(fullPath, navArgs)
    }

    fun saveState(){
        savedStateHandle[FOLDERS_UI_STATE_KEY] = _uiState.value.toParcelable()
    }

    private fun loadUiState(): StateFlow<FoldersUiState> {
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
                    val relativePath = FoldersRelativeUiState(
                        path = it.value,
                        foldersRelativeClickListener
                    )

                    FoldersFullUiState(
                        path = it.key,
                        relativePath = relativePath,
                        onClick = foldersFullClickListener
                    )
                }?.map {
                    FoldersUiState(fullFolders = listOf(it))
                }?.reduceOrNull { acc, foldersUiState ->
                    acc.copy(
                        fullFolders = acc.fullFolders.plus(foldersUiState.fullFolders)
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
        val state = savedStateHandle
            .get<List<ParcelableFoldersFullUiState>>(FOLDERS_UI_STATE_KEY)
            ?.map {
                it.toState(
                    foldersFullOnClick = foldersFullClickListener,
                    foldersRelativeOnClick = foldersRelativeClickListener
                )
            }
            ?.map {
                FoldersUiState(fullFolders = listOf(it))
            }?.reduceOrNull { acc, foldersUiState ->
                acc.copy(
                    fullFolders = acc.fullFolders.plus(foldersUiState.fullFolders)
                )
            }

        state?.let {
            viewModelScope.launch {
                _uiState.emit(it)
            }
        }
    }
}