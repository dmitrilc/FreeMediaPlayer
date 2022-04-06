package com.dimitrilc.freemediaplayer.ui.viewmodel

import android.support.v4.media.MediaBrowserCompat
import androidx.lifecycle.*
import com.dimitrilc.freemediaplayer.domain.GenerateGlobalPlaylistAndActiveItemUseCase
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetActiveMediaItemOnceUseCase
import com.dimitrilc.freemediaplayer.domain.mediaitem.GetMediaItemsInGlobalPlaylistOnceUseCase
import com.dimitrilc.freemediaplayer.domain.activemedia.InsertDefaultActiveMediaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import javax.inject.Inject

private const val TAG = "APP_VIEW_MODEL"

@HiltViewModel
class AppViewModel @Inject constructor(
    private val generateGlobalPlaylistAndActiveItemUseCase: GenerateGlobalPlaylistAndActiveItemUseCase,
    private val insertDefaultActiveMediaUseCase: InsertDefaultActiveMediaUseCase,
    private val getActiveMediaItemOnceUseCase: GetActiveMediaItemOnceUseCase,
    private val getMediaItemsInGlobalPlaylistOnceUseCase: GetMediaItemsInGlobalPlaylistOnceUseCase
): ViewModel() {
    val audioBrowser = MutableLiveData<MediaBrowserCompat>()

    suspend fun getActiveMediaItemOnce() = getActiveMediaItemOnceUseCase()
    suspend fun getMediaItemsInGlobalPlaylistOnce() = getMediaItemsInGlobalPlaylistOnceUseCase()

    fun generateGlobalPlaylistAndActiveItem(currentPath: String, selectedIndex: Int, isAudio: Boolean)
        = generateGlobalPlaylistAndActiveItemUseCase(currentPath, selectedIndex, isAudio)


    fun insertActiveMedia(currentItemPos: Long, id: Long) {
        insertDefaultActiveMediaUseCase(currentItemPos, id)
    }
}