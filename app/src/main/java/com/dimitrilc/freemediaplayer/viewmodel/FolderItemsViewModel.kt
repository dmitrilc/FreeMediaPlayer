package com.dimitrilc.freemediaplayer.viewmodel

import androidx.lifecycle.ViewModel
import com.dimitrilc.freemediaplayer.room.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FolderItemsViewModel @Inject constructor(appDb: AppDatabase) : ViewModel(){
    val audioFolderItemsLiveData = appDb.mediaItemDao().getCurrentAudioFolderItems()
    val videoFolderItemsLiveData = appDb.mediaItemDao().getCurrentVideoFolderItems()
}