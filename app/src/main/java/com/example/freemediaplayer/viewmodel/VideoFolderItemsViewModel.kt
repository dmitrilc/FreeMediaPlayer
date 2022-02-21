package com.example.freemediaplayer.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.freemediaplayer.entities.Audio
import com.example.freemediaplayer.entities.Video
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VideoFolderItemsViewModel @Inject constructor(): ViewModel() {
    //Also used as playlist if coming from folder
    val currentFolderVideos = MutableLiveData<List<Video>>()

    val currentFolderLocation by lazy {
        currentFolderVideos.value
            ?.first()
            ?.location
    }
}