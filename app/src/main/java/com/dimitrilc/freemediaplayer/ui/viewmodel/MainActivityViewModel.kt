package com.dimitrilc.freemediaplayer.ui.viewmodel

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.lifecycle.*
import com.dimitrilc.freemediaplayer.R
import com.dimitrilc.freemediaplayer.data.proto.BottomNavProto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    app: Application,
    private val bottomNavDataStore: DataStore<BottomNavProto>
    ): AndroidViewModel(app) {

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