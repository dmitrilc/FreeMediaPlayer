package com.dimitrilc.freemediaplayer.ui.viewmodel

import androidx.lifecycle.*
import com.dimitrilc.freemediaplayer.data.repos.MediaItemRepository
import com.dimitrilc.freemediaplayer.data.repos.MediaStoreRepository
import com.dimitrilc.freemediaplayer.ui.state.FolderItemsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderItemsViewModel @Inject constructor(
    private val mediaItemRepository: MediaItemRepository,
    private val mediaStoreRepository: MediaStoreRepository
    ) : ViewModel(){
    private val _folderItemsUiState = MutableLiveData<List<FolderItemsUiState>>()
    val folderItemsUiState = _folderItemsUiState as LiveData<List<FolderItemsUiState>>

    fun refreshFolderItemsUiState(isAudio: Boolean, location: String) {
        viewModelScope.launch(Dispatchers.IO){
            val items = if (isAudio){
                mediaItemRepository.getAllAudioByLocation(location)
            } else {
                mediaItemRepository.getAllVideoByLocation(location)
            }

            val result = items.map {
                FolderItemsUiState(
                    title = it.title,
                    album = it.album,
                    thumbnail = mediaStoreRepository.getThumbnail(it.albumArtUri, if (isAudio) null else it.id)
                )
            }

            _folderItemsUiState.postValue(result)
        }
    }
}