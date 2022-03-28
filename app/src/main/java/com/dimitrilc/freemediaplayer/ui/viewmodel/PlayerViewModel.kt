package com.dimitrilc.freemediaplayer.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.dimitrilc.freemediaplayer.getThumbnail
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(private val app: Application) : ViewModel() {
    fun getThumb(albumArtUri: String): Bitmap? {
        return app.getThumbnail(albumArtUri, null)
    }
}
