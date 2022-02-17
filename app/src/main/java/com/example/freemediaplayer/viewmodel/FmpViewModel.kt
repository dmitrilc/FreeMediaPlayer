package com.example.freemediaplayer.viewmodel

import android.Manifest
import android.app.Application
import android.graphics.Bitmap
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.datastore.core.DataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.freemediaplayer.R
import com.example.freemediaplayer.entities.Audio
import com.example.freemediaplayer.entities.Video
import com.example.freemediaplayer.fragments.RepeatMode
import com.example.freemediaplayer.pojos.FolderData
import com.example.freemediaplayer.proto.BottomNavProto
import com.example.freemediaplayer.room.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private const val TAG = "VIEWMODEL"

@HiltViewModel
class FmpViewModel @Inject constructor(private val app: Application): AndroidViewModel(app) {

    @Inject
    lateinit var appDatabase: AppDatabase

    @Inject
    lateinit var bottomNavDataStore: DataStore<BottomNavProto>

    //TODO Move to Activity/Fragment
    fun isReadExternalStoragePermGranted() =
        ContextCompat.checkSelfPermission(
            app.applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PermissionChecker.PERMISSION_GRANTED

    val allAudios by lazy { getAllAudio() }

    var allFolders: List<FolderData>? = null
    //var currentAudioLocation: Int? = null

    //Also used as playlist
    var currentAudioFiles: List<Audio> = listOf()

    val currentPlaylist: MutableList<Audio> = mutableListOf()

    var repeatMode: RepeatMode = RepeatMode.NONE

    var currentAudio: Audio? = null

    val loadedThumbnails: MutableMap<String, Bitmap?> = mutableMapOf()

    //private var audioPlayerThumbnail: Bitmap? = reloadThumbnail()

    var audioFolderData: List<FolderData>? = null

    fun getAudioPlayerThumbnail(): Bitmap? {
        return loadedThumbnails[currentAudio?.album]
    }

    fun insertAudio(audio: Audio) = runBlocking {
        appDatabase.audioDao().insert(audio)
    }

    fun insertAudios(audios: Collection<Audio>) = runBlocking {
        appDatabase.audioDao().insertAll(audios)
    }

    fun getAllAudio(): List<Audio> = runBlocking {
        appDatabase.audioDao().getAll()
    }

    fun getAudioUris(): List<String> = runBlocking {
        appDatabase.audioDao().getUris()
    }

    fun getVideoUris(): List<String> = runBlocking {
        appDatabase.videoDao().getUris()
    }

    fun getVideoTypes(): List<String> = runBlocking {
        appDatabase.videoDao().getPaths()
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

    private fun bottomNavStateToId(state: BottomNavProto.State) =
        if (state == BottomNavProto.State.MUSIC) R.id.audio_folders_path
        else R.id.video_folders_path

    private fun idToBottomNavState(id: Int) =
        if (id == R.id.audio_folders_path) BottomNavProto.State.MUSIC
        else BottomNavProto.State.VIDEOS

}