package com.example.freemediaplayer.viewmodel

import android.app.Application
import android.content.ContentUris
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.freemediaplayer.entities.Video
import com.example.freemediaplayer.isSameOrAfterQ
import com.example.freemediaplayer.room.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.FileNotFoundException
import javax.inject.Inject

private const val TAG = "VIDEOS_VIEW_MODEL"

@HiltViewModel
class VideosViewModel @Inject constructor(
    private val app: Application,
    private val appDatabase: AppDatabase
): AndroidViewModel(app) {

    val globalVideoPlaylist = mutableListOf<Video>()

    val activeVideo = MutableLiveData<Video>()

    private var isAllVideosScanned = false

    private val allVideos: List<Video> by lazy { scanVideos() }

    val allVideosLiveData = liveData {
        emit(allVideos)
    }

    val loadedThumbnails: MutableLiveData<MutableMap<String, Bitmap?>> = MutableLiveData(mutableMapOf())

    fun loadThumbnail(video: Video){
        loadedThumbnails.value?.let { thumbnailMap ->
            val album = video.album
            val isAlbumThumbnailLoaded = thumbnailMap.containsKey(album)

            if (!isAlbumThumbnailLoaded) {
                if (isSameOrAfterQ()) { //TODO check if thumbnail exists before querying
                    try {
                        val thumbnail = app.contentResolver.loadThumbnail(
                            video.uri,
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

    private suspend fun insertVideos(videos: Collection<Video>) {
        appDatabase.videoDao().insertAll(videos)
    }

    fun postActiveVideo(position: Int){
        activeVideo.postValue(globalVideoPlaylist[position])
    }

    private fun scanVideos(): List<Video> {
        if (isAllVideosScanned) {
            return allVideos
        }

        val videos = mutableListOf<Video>()

        val collection = if (isSameOrAfterQ()) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val projection = mutableListOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.ALBUM,
            MediaStore.Video.Media.DATA
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
            val idColIndex = cursor.getColumnIndex(MediaStore.Video.Media._ID)
            val dataColIndex = cursor.getColumnIndex(MediaStore.Video.Media.DATA)
            val titleColIndex = cursor.getColumnIndex(MediaStore.Video.Media.TITLE)
            val albumColIndex = cursor.getColumnIndex(MediaStore.Video.Media.ALBUM)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColIndex)
                val data = cursor.getString(dataColIndex)

                val uri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val displayName = data.substringAfterLast('/')
                val location = data.substringBeforeLast('/')

                val video = Video(
                    id = id,
                    uri = uri,
                    data = data,
                    displayName = displayName,
                    title = cursor.getString(titleColIndex),
                    album = cursor.getString(albumColIndex),
                    location = location
                )

                videos.add(video)
            }
        }

        isAllVideosScanned = true

        viewModelScope.launch(Dispatchers.IO) {
            insertVideos(allVideos)
        }

        return videos
    }

}