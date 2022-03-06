package com.example.freemediaplayer.viewmodel

import android.app.Application
import android.content.ContentUris
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.freemediaplayer.entities.MediaItem
import com.example.freemediaplayer.entities.ui.FolderItemsUi
import com.example.freemediaplayer.entities.ui.ParentPath
import com.example.freemediaplayer.entities.ui.ParentPathWithRelativePaths
import com.example.freemediaplayer.isSameOrAfterQ
import com.example.freemediaplayer.room.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "FoldersViewModel"

@HiltViewModel
class FoldersViewModel @Inject constructor(
    private val app: Application,
    private val appDb: AppDatabase)
    : ViewModel() {

    val audioFoldersLiveData = liveData<List<ParentPathWithRelativePaths>> {
        appDb
            .parentPathWithRelativePathDao()
            .getAudioParentPathWithRelativePaths()
            .collect {
                emit(it)
            }
    }

    val videoFoldersLiveData = liveData<List<ParentPathWithRelativePaths>> {
        appDb
            .parentPathWithRelativePathDao()
            .getVideoParentPathWithRelativePaths()
            .collect {
                emit(it)
            }
    }

    fun updateCurrentFolderFullPath(fullPath: String){
        viewModelScope.launch {
            appDb.folderItemsUiDao().insertCurrentFolderItemsUi(
                FolderItemsUi(fullPath = fullPath)
            )
        }
    }

    suspend fun updateParentPath(parentPath: ParentPath) {
        appDb.parentPathWithRelativePathDao().updateParentPath(parentPath)
    }
}