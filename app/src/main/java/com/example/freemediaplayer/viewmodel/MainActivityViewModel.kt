package com.example.freemediaplayer.viewmodel

import android.Manifest
import android.app.Application
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.datastore.core.DataStore
import androidx.lifecycle.*
import com.example.freemediaplayer.R
import com.example.freemediaplayer.proto.BottomNavProto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val app: Application,
    private val bottomNavDataStore: DataStore<BottomNavProto>
    ): AndroidViewModel(app) {

    //val folderType = MutableLiveData<FolderType>()

    //@Inject
    //lateinit var bottomNavDataStore: DataStore<BottomNavProto>

    fun isReadExternalStoragePermGranted() =
        ContextCompat.checkSelfPermission(
            app.applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PermissionChecker.PERMISSION_GRANTED

    fun getBottomNavState() = bottomNavDataStore.data
        .map { nav ->
            bottomNavStateToId(nav.state)
        }

    fun persistBottomNavState(id: Int) {
        val state = idToBottomNavState(id)

        viewModelScope.launch {
            bottomNavDataStore.updateData { currentState ->
                currentState.toBuilder()
                    .setState(state)
                    .build()
            }
        }
    }

    private fun bottomNavStateToId(state: BottomNavProto.State) =
        if (state == BottomNavProto.State.MUSIC) R.id.audio_folders_path
        else R.id.video_folders_path

    private fun idToBottomNavState(id: Int) =
        if (id == R.id.audio_folders_path) BottomNavProto.State.MUSIC
        else BottomNavProto.State.VIDEOS

}