package com.example.freemediaplayer.viewmodel

import android.Manifest
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.freemediaplayer.Audio
import com.example.freemediaplayer.FolderData
import com.example.freemediaplayer.Video
import com.example.freemediaplayer.room.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private const val TAG = "VIEW_MODEL"

@HiltViewModel
class FmpViewModel @Inject constructor(): ViewModel() {

    @Inject
    lateinit var appDatabase: AppDatabase

    fun insertAudio(audio: Audio) = runBlocking {
        appDatabase.audioDao().insert(audio)
    }

    fun insertAudios(audios: List<Audio>) = runBlocking {
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

    fun insertVideos(videos: List<Video>) = runBlocking {
        appDatabase.videoDao().insertAll(videos)
    }

}