package com.dimitrilc.freemediaplayer.ui.viewmodel

import android.os.Bundle
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.lifecycle.*
import com.dimitrilc.freemediaplayer.R
import com.dimitrilc.freemediaplayer.data.proto.BottomNavProto
import com.dimitrilc.freemediaplayer.domain.mediastore.ScanForMediaFilesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MAIN_VIEW_MODEL"

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val bottomNavDataStore: DataStore<BottomNavProto>,
    private val scanForMediaFilesUseCase: ScanForMediaFilesUseCase
    ): ViewModel() {

    fun saveNavState(bundle: Bundle?){
        if (bundle != null){
            savedStateHandle["NAV_STATE"] = bundle
        }
    }

    fun getSavedNavState(): Bundle? {
        return savedStateHandle.get<Bundle>("NAV_STATE")
    }

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
            BottomNavProto.State.AUDIO_FOLDERS -> R.id.audio_folders_path
            BottomNavProto.State.AUDIO_FOLDER_ITEMS -> R.id.audio_folder_items_path
            BottomNavProto.State.AUDIO_PLAYER -> R.id.audio_player_path
            BottomNavProto.State.VIDEOS_FOLDERS -> R.id.video_folders_path
            BottomNavProto.State.VIDEO_FOLDER_ITEMS -> R.id.video_folder_items_path
            BottomNavProto.State.VIDEO_PLAYER -> R.id.video_player_path
            BottomNavProto.State.PLAYLISTS -> R.id.playlists_path
            else -> R.id.active_playlist_path
        }
    }

    private fun idToBottomNavState(id: Int): BottomNavProto.State {
        return when(id){
            R.id.audio_folders_path -> BottomNavProto.State.AUDIO_FOLDERS
            R.id.audio_folder_items_path -> BottomNavProto.State.AUDIO_FOLDER_ITEMS
            R.id.audio_player_path -> BottomNavProto.State.AUDIO_PLAYER
            R.id.video_folders_path -> BottomNavProto.State.VIDEOS_FOLDERS
            R.id.video_folder_items_path -> BottomNavProto.State.VIDEO_FOLDER_ITEMS
            R.id.video_player_path -> BottomNavProto.State.VIDEO_PLAYER
            R.id.playlists_path -> BottomNavProto.State.PLAYLISTS
            else -> BottomNavProto.State.ACTIVE_PLAYLIST
        }
    }

    fun scanForMediaFiles() {
        scanForMediaFilesUseCase()
    }

}