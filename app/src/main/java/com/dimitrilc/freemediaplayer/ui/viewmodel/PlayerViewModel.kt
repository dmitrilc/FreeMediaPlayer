package com.dimitrilc.freemediaplayer.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.dimitrilc.freemediaplayer.domain.GetThumbByUriUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val getThumbByUriUseCase: GetThumbByUriUseCase
    ) : ViewModel() {

    fun getAudioThumb(albumArtUri: String) = getThumbByUriUseCase(albumArtUri, null)

}