package com.example.freemediaplayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freemediaplayer.entities.MediaItem
import com.example.freemediaplayer.room.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FolderItemsViewModel @Inject constructor(private val appDb: AppDatabase) : ViewModel(){
    val audioFolderItemsLiveData = appDb.mediaItemDao().getCurrentAudioFolderItems()

    val videoFolderItemsLiveData = appDb.mediaItemDao().getCurrentVideoFolderItems()

    fun updateGlobalPlayList(playlist: List<MediaItem>){
        viewModelScope.launch {
            appDb.globalPlaylistDao().replacePlaylist(playlist)
        }
    }

    fun updateGlobalActiveItem(item: MediaItem){
        viewModelScope.launch {
            appDb.activeMediaItemDao().insert(item)
        }
    }
}