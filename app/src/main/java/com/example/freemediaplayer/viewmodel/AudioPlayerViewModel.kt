package com.example.freemediaplayer.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(): ViewModel()  {
    val playerPosition = MutableLiveData<Int>()

    val maxPlayerDuration = MutableLiveData<Int>()

    val isGlobalSelfLooping = MutableLiveData<Boolean>(false)

    val isPlaying = MutableLiveData<Boolean>(true)
}