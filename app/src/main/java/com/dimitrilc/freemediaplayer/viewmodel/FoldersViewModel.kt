package com.dimitrilc.freemediaplayer.viewmodel

import androidx.lifecycle.*
import com.dimitrilc.freemediaplayer.entities.ui.ParentPath
import com.dimitrilc.freemediaplayer.room.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "FOLDERS_VIEW_MODEL"

@HiltViewModel
class FoldersViewModel @Inject constructor(private val appDb: AppDatabase) : ViewModel() {

    val audioFoldersLiveData = appDb
        .parentPathWithRelativePathDao()
        .getAudioParentPathWithRelativePaths()

    val videoFoldersLiveData = appDb
        .parentPathWithRelativePathDao()
        .getVideoParentPathWithRelativePaths()

    fun updateParentPath(parentPath: ParentPath) {
        viewModelScope.launch(Dispatchers.IO){
            appDb.parentPathWithRelativePathDao()
                .updateParentPath(parentPath)
        }
    }

}