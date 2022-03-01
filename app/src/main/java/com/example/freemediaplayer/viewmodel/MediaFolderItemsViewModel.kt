package com.example.freemediaplayer.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.freemediaplayer.entities.MediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MediaFolderItemsViewModel @Inject constructor() : ViewModel() {
    //Also used as playlist if coming from folder
    //val currentFolderMediaItems = MutableLiveData<List<MediaItem>>()
//
//    val currentFolderLocation by lazy {
//        currentFolderMediaItems.value
//            ?.first()
//            ?.location
//    }
}