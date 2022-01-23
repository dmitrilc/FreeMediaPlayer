package com.example.freemediaplayer.viewmodel

import android.Manifest
import android.app.Application
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.datastore.core.DataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.freemediaplayer.R
import com.example.freemediaplayer.entities.Audio
import com.example.freemediaplayer.entities.Video
import com.example.freemediaplayer.proto.BottomNavProto
import com.example.freemediaplayer.room.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private const val TAG = "VIEW_MODEL"

@HiltViewModel
class FmpViewModel @Inject constructor(private val app: Application): AndroidViewModel(app) {

    @Inject
    lateinit var appDatabase: AppDatabase

    @Inject
    lateinit var bottomNavDataStore: DataStore<BottomNavProto>

    fun isReadExternalStoragePermGranted() =
        ContextCompat.checkSelfPermission(
            app.applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PermissionChecker.PERMISSION_GRANTED

    fun insertAudio(audio: Audio) = runBlocking {
        appDatabase.audioDao().insert(audio)
    }

    fun insertAudios(audios: List<Audio>) = runBlocking {
        appDatabase.audioDao().insertAll(audios)
    }

    fun getAllAudio(): List<Audio> = runBlocking {
        appDatabase.audioDao().getAll()
    }

    fun getAudioTypes(): List<String> = runBlocking {
        appDatabase.audioDao().getTypes()
    }

    fun getAudioUris(): List<String> = runBlocking {
        appDatabase.audioDao().getUris()
    }

    fun getVideoUris(): List<String> = runBlocking {
        appDatabase.videoDao().getUris()
    }

    fun getVideoTypes(): List<String> = runBlocking {
        appDatabase.videoDao().getTypes()
    }

    fun getAllVideos(): List<Video> = runBlocking {
        appDatabase.videoDao().getAll()
    }

    fun insertVideos(videos: List<Video>) = runBlocking {
        appDatabase.videoDao().insertAll(videos)
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

    private fun bottomNavStateToId(state: BottomNavProto.State) = when (state) {
        BottomNavProto.State.MUSIC -> R.id.music
        BottomNavProto.State.VIDEOS -> R.id.videos
        BottomNavProto.State.PLAYLISTS -> R.id.playlists
        else -> R.id.radio
    }

    private fun idToBottomNavState(id: Int) = when (id) {
        R.id.music -> BottomNavProto.State.MUSIC
        R.id.videos -> BottomNavProto.State.VIDEOS
        R.id.playlists -> BottomNavProto.State.PLAYLISTS
        else -> BottomNavProto.State.RADIO
    }

}