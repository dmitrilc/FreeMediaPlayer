package com.dimitrilc.freemediaplayer.ui.viewmodel

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import com.dimitrilc.freemediaplayer.R
import com.dimitrilc.freemediaplayer.data.proto.BottomNavProto
import com.dimitrilc.freemediaplayer.data.repos.MediaManager
import com.dimitrilc.freemediaplayer.domain.mediastore.ScanForMediaFilesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    //private val app: Application,
    private val bottomNavDataStore: DataStore<BottomNavProto>,
    private val scanForMediaFilesUseCase: ScanForMediaFilesUseCase
    ): ViewModel() {

    fun getBottomNavState() = bottomNavDataStore.data
        .map { nav ->
            bottomNavStateToId(nav.state)
        }

    fun persistBottomNavState(id: Int) {
        val state = idToBottomNavState(id)

        if (state != null){
            viewModelScope.launch {
                bottomNavDataStore.updateData { currentState ->
                    currentState.toBuilder()
                        .setState(state)
                        .build()
                }
            }
        }
    }

    private fun bottomNavStateToId(state: BottomNavProto.State): Int? {
        return when(state){
            BottomNavProto.State.MUSIC -> R.id.audio_folders_path
            BottomNavProto.State.VIDEOS -> R.id.video_folders_path
            BottomNavProto.State.PLAYLISTS -> R.id.playlists_path
            else -> null
        }
    }

    private fun idToBottomNavState(id: Int): BottomNavProto.State? {
        return when(id){
            R.id.audio_folders_path -> BottomNavProto.State.MUSIC
            R.id.video_folders_path -> BottomNavProto.State.VIDEOS
            R.id.playlists_path -> BottomNavProto.State.PLAYLISTS
            else -> null
        }
    }

    fun scanForMediaFiles() {
        scanForMediaFilesUseCase()
    }

}