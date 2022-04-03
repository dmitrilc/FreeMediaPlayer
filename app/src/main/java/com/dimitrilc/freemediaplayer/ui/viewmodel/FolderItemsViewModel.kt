package com.dimitrilc.freemediaplayer.ui.viewmodel

import androidx.lifecycle.*
import com.dimitrilc.freemediaplayer.domain.GetItemsByLocationUseCase
import com.dimitrilc.freemediaplayer.domain.GetThumbByUriUseCase
import com.dimitrilc.freemediaplayer.ui.state.FolderItemsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FolderItemsViewModel @Inject constructor(
    getItemsByLocationUseCase: GetItemsByLocationUseCase,
    private val getThumbByUriUseCase: GetThumbByUriUseCase
    ) : ViewModel() {

    var isAudio = true
    var location = ""

    val folderItemsUiState by lazy {
        getItemsByLocationUseCase(isAudio, location).map { items ->
            items.map {
                val videoId = if (isAudio) null else it.id

                FolderItemsUiState(
                    title = it.title,
                    album = it.album,
                    thumbnail = getThumbByUriUseCase(
                        it.albumArtUri,
                        videoId
                    )
                )
            }
        }
    }
}