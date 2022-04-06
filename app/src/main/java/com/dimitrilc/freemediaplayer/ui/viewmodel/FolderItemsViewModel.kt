package com.dimitrilc.freemediaplayer.ui.viewmodel

import androidx.lifecycle.*
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetMediaItemsByLocationUseCase
import com.dimitrilc.freemediaplayer.domain.mediastore.GetThumbByUriUseCase
import com.dimitrilc.freemediaplayer.domain.worker.GetUpdateActiveMediaWorkerInfoObservable
import com.dimitrilc.freemediaplayer.ui.state.FolderItemsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FolderItemsViewModel @Inject constructor(
    getMediaItemsByLocationUseCase: GetMediaItemsByLocationUseCase,
    private val getUpdateActiveMediaWorkerInfoObservable: GetUpdateActiveMediaWorkerInfoObservable,
    private val getThumbByUriUseCase: GetThumbByUriUseCase
    ) : ViewModel() {

    var isAudio = true
    var location = ""

    val folderItemsUiState by lazy {
        getMediaItemsByLocationUseCase(isAudio, location).map { items ->
            items!!.map {
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

    fun getUpdateActiveMediaWorkInfoObservable(uuid: String) = getUpdateActiveMediaWorkerInfoObservable(uuid)
}