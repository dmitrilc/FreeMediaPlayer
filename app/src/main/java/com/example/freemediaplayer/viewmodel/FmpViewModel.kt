package com.example.freemediaplayer.viewmodel

import androidx.lifecycle.ViewModel
import com.example.freemediaplayer.entities.Audio
import com.example.freemediaplayer.entities.Video
import com.example.freemediaplayer.room.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
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