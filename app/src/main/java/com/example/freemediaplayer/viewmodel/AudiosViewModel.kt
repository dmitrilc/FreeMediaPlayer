package com.example.freemediaplayer.viewmodel

import android.app.Application
import android.content.ContentUris
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.lifecycle.*
import com.example.freemediaplayer.entities.Audio
import com.example.freemediaplayer.entities.Video
import com.example.freemediaplayer.isSameOrAfterQ
import com.example.freemediaplayer.room.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.FileNotFoundException
import javax.inject.Inject

private const val TAG = "FMP_VIEW_MODEL"

@HiltViewModel
class AudiosViewModel @Inject constructor(
    private val app: Application,
    private val appDatabase: AppDatabase
    ): AndroidViewModel(app) {

    val currentPlaylist = MutableLiveData<List<Audio>>()

    val activeAudio = MutableLiveData<Audio>()

    fun postCurrentPlaylist(allAudios: List<Audio>, currentFolderLocation: String){
        val playlist = allAudios.filter { audio ->
            audio.location == currentFolderLocation
        }

        currentPlaylist.postValue(playlist)
    }

    fun postActiveAudio(position: Int){
        activeAudio.postValue(currentPlaylist.value?.get(position))
    }

    val loadedThumbnails: MutableLiveData<MutableMap<String, Bitmap?>> = MutableLiveData(mutableMapOf())

    fun loadThumbnail(audio: Audio){
        loadedThumbnails.value?.let { thumbnailMap ->
            val album = audio.album
            val isAlbumThumbnailLoaded = thumbnailMap.containsKey(album)

            if (!isAlbumThumbnailLoaded) {
                if (isSameOrAfterQ()) { //TODO check if thumbnail exists before querying
                    try {
                        val thumbnail = app.contentResolver.loadThumbnail(
                            audio.uri,
                            Size(300, 300),
                            null
                        )

                        thumbnailMap[album] = thumbnail

                        loadedThumbnails.postValue(thumbnailMap)
                    } catch (e: FileNotFoundException) {
                        //TODO Implement
                        Log.d(TAG, e.toString())
                    }
                }
            }
        }
    }

    fun insertVideos(videos: List<Video>) = runBlocking {
        appDatabase.videoDao().insertAll(videos)
    }

    private var isAllAudiosScanned = false

    private val allAudios: List<Audio> by lazy { scanAudios() }

    val allAudiosLiveData = liveData {
        emit(allAudios)
    }

    //TODO Move to viewmodel or service
    private fun scanAudios(): List<Audio> {
        if (isAllAudiosScanned){
            return allAudios
        }

        val audios = mutableListOf<Audio>()

        val collection =
            if (isSameOrAfterQ()) {
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

        val projection = mutableListOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA
        )

        val selection = null
        val selectionArgs = null
        val sortOrder = null

        app.contentResolver.query(
            collection,
            projection.toTypedArray(),
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val dataColIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val titleColIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val albumColIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColIndex)
                val data = cursor.getString(dataColIndex)

                val uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val displayName = data.substringAfterLast('/')
                val location = data.substringBeforeLast('/')

                val audio = Audio(
                    id = id,
                    uri = uri,
                    data = data,
                    displayName = displayName,
                    title = cursor.getString(titleColIndex),
                    album = cursor.getString(albumColIndex),
                    location = location
                )

                audios.add(audio)
            }
        }

        isAllAudiosScanned = true

        viewModelScope.launch(Dispatchers.IO) {
            insertAudios(allAudios)
        }

        return audios
    }

    //TODO Remove runblocking
    private suspend fun insertAudios(audios: Collection<Audio>) {
        appDatabase.audioDao().insertAll(audios)
    }

}