package com.example.freemediaplayer.viewmodel

import android.app.Application
import android.content.ContentUris
import android.graphics.Bitmap
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import android.util.Size
import androidx.lifecycle.*
import com.example.freemediaplayer.entities.MediaItem
import com.example.freemediaplayer.isSameOrAfterQ
import com.example.freemediaplayer.entities.ui.ParentPath
import com.example.freemediaplayer.entities.ui.ParentPathWithRelativePaths
import com.example.freemediaplayer.entities.ui.RelativePath
import com.example.freemediaplayer.room.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import javax.inject.Inject

private const val TAG = "MEDIA_ITEMS_VIEW_MODEL"

@HiltViewModel
class MediaItemsViewModel @Inject constructor(
    private val app: Application,
    private val appDb: AppDatabase
): ViewModel() {

    val audioBrowser = MutableLiveData<MediaBrowserCompat>()
    val currentFolderFullPathLiveData = MutableLiveData<String>()
    val globalPlaylist = MutableLiveData<MutableList<MediaItem>>()
    val activeMedia = MutableLiveData<MediaItem>()
    val loadedThumbnails: MutableLiveData<MutableMap<String, Bitmap?>> = MutableLiveData(mutableMapOf())

/*    private val allAudios by lazy {
        queryAudios()
        //getAudioLocations()
    }

    private val allVideos by lazy {
        queryVideos()
    }*/

/*    val audioFolderItemsLiveData = flow<List<MediaItem>> {
        emit(allAudios.filter { it.location == currentFolderFullPathLiveData.value })
    }

    val videoFolderItemsLiveData = flow<List<MediaItem>> {
        emit(allVideos.filter { it.location == currentFolderFullPathLiveData.value })
    }*/

/*    val audioFoldersLiveData by lazy {
        MutableLiveData<List<Folders>>(
            allAudios
                .asSequence()
                .filter { it.isAudio }
                .distinctBy { it.location }
                .map { it.location }
                .groupBy({ it.substringBeforeLast('/') }) {
                    it.substringAfterLast('/')
                }
                .map {
                    Folders(
                        parentPath = it.key,
                        relativePaths = it.value
                    )
                }
                .toList()
        )
    }*/

/*    val audioFoldersLiveData = liveData<List<ParentPathWithRelativePaths>> {
        appDb
            .parentPathWithRelativePathDao()
            .getAudioParentPathWithRelativePaths()
            .collect {
                emit(it)
            }
    }

    val videoFoldersLiveData = liveData<List<ParentPathWithRelativePaths>> {
        appDb
            .parentPathWithRelativePathDao()
            .getVideoParentPathWithRelativePaths()
            .collect {
                emit(it)
            }
    }*/

/*    suspend fun insertParentPath(parentPath: ParentPath){
        appDb.parentPathWithRelativePathDao().insertParentPath(parentPath)
    }*/

/*    suspend fun audioFolders() = appDb
        .parentPathWithRelativePathDao()
        .getAudioParentPathWithRelativePaths()*/

/*    val audioFoldersLiveData by lazy {
        MutableLiveData<List<Folders>>(
            allAudios
                .asSequence()
                .filter { it.isAudio }
                .distinctBy { it.location }
                .map { it.location }
                .groupBy({ it.substringBeforeLast('/') }) {
                    it.substringAfterLast('/')
                }
                .map {
                    Folders(
                        parentPath = it.key,
                        relativePaths = it.value
                    )
                }
                .toList()
        )
    }*/

/*    val videoFoldersLiveData by lazy {
        MutableLiveData<List<ParentPath>>(
            allVideos
                .asSequence()
                .filter { !it.isAudio }
                .distinctBy { it.location }
                .map { it.location }
                .groupBy({ it.substringBeforeLast('/') }) {
                    it.substringAfterLast('/')
                }
                .map {
                    ParentPath(
                        parentPath = it.key,
                        relativePaths = it.value
                    )
                }
                .toList()
        )
    }*/

//    private suspend fun getAudioLocations() : List<MediaItem>{
//        return appDb.mediaItemDao().getAudioLocations()
//    }

/*    private suspend fun insertMediaItems(items: Collection<MediaItem>) {
        appDb.mediaItemDao().insertAll(items)
    }*/

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

/*    fun queryAudios(): List<MediaItem> {
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

        viewModelScope.launch {
            insertMediaItems(allAudios)

            val tmpMap = allAudios
                .asSequence()
                .distinctBy { it.location }
                .map { it.location }
                .groupBy({ it.substringBeforeLast('/') }) {
                    it.substringAfterLast('/')
                }

            appDb.parentPathWithRelativePathDao().insertAudioParentPathsWithRelativePaths(tmpMap)
        }

        return allAudios
    }

    fun queryVideos(): List<MediaItem> {
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

        viewModelScope.launch {
            insertMediaItems(allVideos)

            val tmpMap = allVideos
                .asSequence()
                .distinctBy { it.location }
                .map { it.location }
                .groupBy({ it.substringBeforeLast('/') }) {
                    it.substringAfterLast('/')
                }

            appDb.parentPathWithRelativePathDao().insertVideoParentPathsWithRelativePaths(tmpMap)
        }

        return allVideos
    }

    suspend fun updateParentPath(parentPath: ParentPath) {
        appDb.parentPathWithRelativePathDao().updateParentPath(parentPath)
    }*/

    fun queryAudios(): List<MediaItem> {
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

        viewModelScope.launch {
            insertMediaItems(allAudios)

            val tmpMap = allAudios
                .asSequence()
                .distinctBy { it.location }
                .map { it.location }
                .groupBy({ it.substringBeforeLast('/') }) {
                    it.substringAfterLast('/')
                }

            appDb.parentPathWithRelativePathDao().insertAudioParentPathsWithRelativePaths(tmpMap)
        }

        return allAudios
    }

    fun queryVideos(): List<MediaItem> {
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

        viewModelScope.launch {
            insertMediaItems(allVideos)

            val tmpMap = allVideos
                .asSequence()
                .distinctBy { it.location }
                .map { it.location }
                .groupBy({ it.substringBeforeLast('/') }) {
                    it.substringAfterLast('/')
                }

            appDb.parentPathWithRelativePathDao().insertVideoParentPathsWithRelativePaths(tmpMap)
        }

        return allVideos
    }

    private suspend fun insertMediaItems(items: Collection<MediaItem>) {
        appDb.mediaItemDao().insertAll(items)
    }

}