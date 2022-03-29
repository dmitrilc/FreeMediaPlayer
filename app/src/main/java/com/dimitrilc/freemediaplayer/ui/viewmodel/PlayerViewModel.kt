package com.dimitrilc.freemediaplayer.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.dimitrilc.freemediaplayer.data.repos.MediaStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val mediaStoreRepository: MediaStoreRepository
    ) : ViewModel() {

    fun getThumb(albumArtUri: String): Bitmap? {
        return mediaStoreRepository.getThumbnail(albumArtUri, null)
    }
}
