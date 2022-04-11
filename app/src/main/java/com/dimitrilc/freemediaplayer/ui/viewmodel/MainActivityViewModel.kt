package com.dimitrilc.freemediaplayer.ui.viewmodel

import androidx.datastore.core.DataStore
import androidx.lifecycle.*
import com.dimitrilc.freemediaplayer.R
import com.dimitrilc.freemediaplayer.data.proto.BottomNavProto
import com.dimitrilc.freemediaplayer.domain.mediastore.ScanForMediaFilesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val bottomNavDataStore: DataStore<BottomNavProto>,
    private val scanForMediaFilesUseCase: ScanForMediaFilesUseCase
    ): ViewModel() {

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

    private fun bottomNavStateToId(state: BottomNavProto.State): Int {
        return when(state){
            BottomNavProto.State.VIDEOS_FOLDERS -> R.id.video_folders_path
            BottomNavProto.State.PLAYLISTS -> R.id.playlists_path
            BottomNavProto.State.SETTINGS -> R.id.settings_path
            else -> R.id.audio_folders_path
        }
    }

    private fun idToBottomNavState(id: Int): BottomNavProto.State {
        return when(id){
            R.id.video_folders_path,
            R.id.video_folder_items_path,
            R.id.video_player_path -> BottomNavProto.State.VIDEOS_FOLDERS
            R.id.playlists_path -> BottomNavProto.State.PLAYLISTS
            R.id.settings_path -> BottomNavProto.State.SETTINGS
            else -> BottomNavProto.State.AUDIO_FOLDERS
        }
    }

    fun scanForMediaFiles() {
        scanForMediaFilesUseCase()
    }

}