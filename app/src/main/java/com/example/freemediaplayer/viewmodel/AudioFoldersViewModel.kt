package com.example.freemediaplayer.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.freemediaplayer.pojos.AdapterFolderData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "AUDIO_FOLDERS_VIEW_MODEL"

@HiltViewModel
class AudioFoldersViewModel @Inject constructor(): ViewModel() {

    val allAudioFoldersLiveData = MutableLiveData<List<AdapterFolderData>>(listOf())

    fun refreshAllAudioFoldersLiveData(position: Int){
        allAudioFoldersLiveData.value?.let {
            val newData = AdapterFolderData(
                it[position].parentPath,
                it[position].relativePaths,
                !it[position].isCollapsed
            )

            val mutableTmp = it.toMutableList()
            mutableTmp.removeAt(position)
            mutableTmp.add(position, newData)

            allAudioFoldersLiveData.postValue(mutableTmp)
        }
    }
}