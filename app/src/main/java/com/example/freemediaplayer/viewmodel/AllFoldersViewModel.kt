package com.example.freemediaplayer.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.freemediaplayer.pojos.AdapterFolderData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val TAG = "FOLDERS_VIEW_MODEL"

@HiltViewModel
class AllFoldersViewModel @Inject constructor(): ViewModel() {

    val allFoldersLiveData = MutableLiveData<List<AdapterFolderData>>(listOf())

    fun refreshAllFoldersLiveData(position: Int){
        allFoldersLiveData.value?.let {
            val newData = AdapterFolderData(
                it[position].parentPath,
                it[position].relativePaths,
                !it[position].isCollapsed
            )

            val mutableTmp = it.toMutableList()
            mutableTmp.removeAt(position)
            mutableTmp.add(position, newData)

            allFoldersLiveData.postValue(mutableTmp)
        }
    }
}