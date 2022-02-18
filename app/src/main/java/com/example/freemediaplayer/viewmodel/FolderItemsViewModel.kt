package com.example.freemediaplayer.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.freemediaplayer.entities.Audio
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FolderItemsViewModel @Inject constructor(): ViewModel() {
    //Also used as playlist if coming from folder
    val currentFolderAudioFiles = MutableLiveData<List<Audio>>()

    val currentFolderLocation by lazy {
        currentFolderAudioFiles.value?.get(0)?.location
    }
}