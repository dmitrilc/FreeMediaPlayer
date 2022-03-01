package com.example.freemediaplayer.viewmodel

import android.app.Application
import android.content.ContentUris
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.lifecycle.*
import com.example.freemediaplayer.entities.MediaItem
import com.example.freemediaplayer.isSameOrAfterQ
import com.example.freemediaplayer.pojos.AdapterFolderData
import com.example.freemediaplayer.room.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import javax.inject.Inject

private const val TAG = "MEDIA_ITEMS_VIEW_MODEL"

@HiltViewModel
class MediaItemsViewModel @Inject constructor(
    private val app: Application,
    private val appDb: AppDatabase
): ViewModel() {

//    class MediaItemsViewModel @Inject constructor(
//        private val app: Application,
//        private val appDb: AppDatabase
//    ): AndroidViewModel(app) {

    val currentFolderFullPathLiveData = MutableLiveData<String>()

    val mediaFoldersLiveData = MutableLiveData<List<AdapterFolderData>>(listOf())

    //val globalPlaylist = mutableListOf<MediaItem>()
    val globalPlaylist = MutableLiveData<MutableList<MediaItem>>()
    val activeMedia = MutableLiveData<MediaItem>()
    val loadedThumbnails: MutableLiveData<MutableMap<String, Bitmap?>> =
        MutableLiveData(mutableMapOf())

    private var isAllMediaScanned = false

    private val allMedia: List<MediaItem> by lazy { scanMedia() }

    val allMediaLiveData = liveData {
        emit(allMedia)
    }

    val allAudiosLiveData = allMediaLiveData.map { items ->
        items.filter { it.isAudio }
    }

    val allVideosLiveData = allMediaLiveData.map { items ->
        items.filter { !it.isAudio }
    }

    private suspend fun insertMediaItems(audios: Collection<MediaItem>) {
        appDb.mediaItemDao().insertAll(audios)
    }

    //TODO Move to viewmodel or service
    private fun scanMedia(): List<MediaItem> {
        if (isAllMediaScanned) {
            return allMedia
        }

        val allMedia = queryAudios() + queryVideos()

        isAllMediaScanned = true

        viewModelScope.launch(Dispatchers.IO) {
            insertMediaItems(allMedia)
        }

        return allMedia
    }

    fun loadThumbnail(item: MediaItem) {
        loadedThumbnails.value?.let { thumbnailMap ->
            val album = item.album
            val isAlbumThumbnailLoaded = thumbnailMap.containsKey(album)

            if (!isAlbumThumbnailLoaded) {
                if (isSameOrAfterQ()) { //TODO check if thumbnail exists before querying
                    try {
                        val thumbnail = app.contentResolver.loadThumbnail(
                            item.uri,
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

    private fun queryAudios(): List<MediaItem> {
        val allAudios = mutableListOf<MediaItem>()

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

                val audio = MediaItem(
                    id = id,
                    uri = uri,
                    data = data,
                    displayName = displayName,
                    title = cursor.getString(titleColIndex),
                    location = location,
                    isAudio = true,
                    album = cursor.getString(albumColIndex),
                )

                allAudios.add(audio)
            }

        }
        return allAudios
    }

    private fun queryVideos(): List<MediaItem> {
        val allVideos = mutableListOf<MediaItem>()

        val collection =
            if (isSameOrAfterQ()) {
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

                val video = MediaItem(
                    id = id,
                    uri = uri,
                    data = data,
                    displayName = displayName,
                    title = cursor.getString(titleColIndex),
                    location = location,
                    isAudio = false,
                    album = cursor.getString(albumColIndex),
                )
                allVideos.add(video)
            }
        }
        return allVideos
    }

}