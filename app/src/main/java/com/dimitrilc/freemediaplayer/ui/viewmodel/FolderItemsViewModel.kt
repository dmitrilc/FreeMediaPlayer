package com.dimitrilc.freemediaplayer.ui.viewmodel

import androidx.lifecycle.*
import com.dimitrilc.freemediaplayer.domain.GetItemsByLocationUseCase
import com.dimitrilc.freemediaplayer.domain.GetThumbByUriUseCase
import com.dimitrilc.freemediaplayer.ui.state.FolderItemsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FolderItemsViewModel @Inject constructor(
    private val getItemsByLocationUseCase: GetItemsByLocationUseCase,
    private val getThumbByUriUseCase: GetThumbByUriUseCase
    ) : ViewModel() {

    fun getFolderItemsUiState(isAudio: Boolean, location: String): LiveData<List<FolderItemsUiState>> {
        return getItemsByLocationUseCase(isAudio, location)
            .map { items ->
                items.map {
                    FolderItemsUiState(
                        title = it.title,
                        album = it.album,
                        thumbnail = getThumbByUriUseCase(
                            it.albumArtUri,
                            if (isAudio) null else it.id
                        )
                )
            }
        }
    }
}