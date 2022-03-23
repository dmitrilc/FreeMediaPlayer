package com.example.freemediaplayer.viewmodel

import android.app.Application
import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Video.Thumbnails.MINI_KIND
import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import android.util.Size
import androidx.lifecycle.*
import androidx.room.withTransaction
import com.example.freemediaplayer.entities.ActiveMediaItem
import com.example.freemediaplayer.entities.GlobalPlaylistItem
import com.example.freemediaplayer.entities.MediaItem
import com.example.freemediaplayer.entities.ui.FolderItemsUi
import com.example.freemediaplayer.isSameOrAfterQ
import com.example.freemediaplayer.room.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.lang.Exception
import javax.inject.Inject

private const val TAG = "MEDIA_ITEMS_VIEW_MODEL"

@HiltViewModel
class MediaItemsViewModel @Inject constructor(
    private val app: Application,
    private val appDb: AppDatabase
): ViewModel() {

    val audioBrowser = MutableLiveData<MediaBrowserCompat>()
    val globalPlaylist = appDb.globalPlaylistDao().getGlobalPlaylist() //TODO Transform in Repository
    val globalPlaylistCache = MutableLiveData<List<MediaItem>>()
    //val activeMedia = appDb.activeMediaItemDao().getObservable() //TODO Transform in Repository

    val activeMediaLiveData = appDb.activeMediaItemDao().getMediaItemLiveData()
    val activeMediaCache = MutableLiveData<MediaItem>()

    suspend fun getCurrentFolderFullPath() = appDb.folderItemsUiDao().getCurrentFolderItemsUi()

    //suspend fun setActiveMedia(item: MediaItem) = appDb.activeMediaItemDao().insert(ActiveMedia(activeItemId = item.id))

    fun getThumbnail(artUri: String, videoId: Long?): Bitmap? {
        var thumbnail: Bitmap? = null

        if (isSameOrAfterQ()) { //TODO check if thumbnail exists before querying
            try {
                thumbnail = app.contentResolver.loadThumbnail(
                    Uri.parse(artUri),
                    Size(300, 300),
                    null
                )
            } catch (e: FileNotFoundException) {
                //TODO Implement default thumb
                Log.d(TAG, e.toString())
            }
        } else {
            thumbnail = if (videoId == null){
                BitmapFactory.decodeFile(artUri)
            } else {
                getVideoThumbBeforeQ(videoId)
            }
        }

        return thumbnail
    }

    private fun getVideoThumbBeforeQ(videoId: Long): Bitmap? {
        return MediaStore.Video.Thumbnails.getThumbnail(
            app.contentResolver,
            videoId,
            MINI_KIND,
            null
        )
    }

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
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
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
            val albumIdColIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColIndex)
                val data = cursor.getString(dataColIndex)

                val uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val displayName = data.substringAfterLast('/')
                val location = data.substringBeforeLast('/')

                val albumId = cursor.getInt(albumIdColIndex)

                val albumArtUri = if (isSameOrAfterQ()){
                    uri.toString()
                } else {
                    getAlbumArtUriBeforeQ(albumId)
                }

                val audio = MediaItem(
                    id = id,
                    uri = uri,
                    data = data,
                    displayName = displayName,
                    title = cursor.getString(titleColIndex),
                    location = location,
                    isAudio = true,
                    album = cursor.getString(albumColIndex),
                    albumId = albumId,
                    albumArtUri = albumArtUri
                )

                allAudios.add(audio)
            }

        }

        viewModelScope.launch {
            val tmpMap = allAudios
                .asSequence()
                .distinctBy { it.location }
                .map { it.location }
                .groupBy({ it.substringBeforeLast('/') }) {
                    it.substringAfterLast('/')
                }

            launch(Dispatchers.IO) {
                insertMediaItems(allAudios)
            }

            launch(Dispatchers.IO) {
                appDb.parentPathWithRelativePathDao().insertAudioParentPathsWithRelativePaths(tmpMap)
            }
        }

        return allAudios
    }

    private fun getAlbumArtUriBeforeQ(albumId: Int): String? {
        val collection = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM_ART
        )
        val selection = "${MediaStore.Audio.Albums._ID} = ?"
        val selectionArgs = arrayOf("$albumId")
        val sortOrder = null

        var albumArtUri: String? = null

        app.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val albumArtColIndex = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)

            while (cursor.moveToNext()) {
                albumArtUri = cursor.getString(albumArtColIndex)
            }
        }

        return albumArtUri
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
                    albumId = -1,
                    albumArtUri = uri.toString()
                )
                allVideos.add(video)
            }
        }

        viewModelScope.launch {
            val tmpMap = allVideos
                .asSequence()
                .distinctBy { it.location }
                .map { it.location }
                .groupBy({ it.substringBeforeLast('/') }) {
                    it.substringAfterLast('/')
                }

            launch(Dispatchers.IO) {
                insertMediaItems(allVideos)
            }

            launch(Dispatchers.IO) {
                appDb.parentPathWithRelativePathDao().insertVideoParentPathsWithRelativePaths(tmpMap)
            }
        }

        return allVideos
    }

    private suspend fun insertMediaItems(items: Collection<MediaItem>) {
        appDb.mediaItemDao().insertAll(items)
    }

    fun updateCurrentFolderFullPath(fullPath: String){
        viewModelScope.launch(Dispatchers.IO){
            appDb.folderItemsUiDao()
                .insertCurrentFolderItemsUi(
                    FolderItemsUi(fullPath = fullPath)
                )
        }
    }

    fun updateGlobalPlaylistAndActiveItem(list: List<MediaItem>, mediaItem: MediaItem) {
        viewModelScope.launch(Dispatchers.IO){
            appDb.withTransaction {
                //Needs to run sequentially because of foreign key constraint
                appDb.globalPlaylistDao().replacePlaylist(list.mapIndexed { index, item ->
                    GlobalPlaylistItem(mId = index.toLong(), mediaItemId = item.id)
                })
                appDb.activeMediaItemDao().insert(ActiveMediaItem(
                    globalPlaylistPosition = list.indexOf(mediaItem).toLong(),
                    mediaItemId = mediaItem.id
                ))
            }
        }
    }

    suspend fun insertActiveMedia(currentItemPos: Long, id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            appDb.activeMediaItemDao().insert(
                ActiveMediaItem(
                    globalPlaylistPosition = currentItemPos,
                    mediaItemId = id
                )
            )
        }
    }

    fun replacePlaylist(playlist: List<MediaItem>){
        viewModelScope.launch(Dispatchers.IO){
            val globalPlaylist = playlist.mapIndexed { index, item ->
                GlobalPlaylistItem(
                    index.toLong(), item.id
                )
            }

            appDb.globalPlaylistDao().replacePlaylist(
                globalPlaylist
            )
        }
    }

    suspend fun getActiveMediaItem() = appDb.activeMediaItemDao().getMediaItemOnce()

    suspend fun getActiveMedia() = appDb.activeMediaItemDao().getOnce()

    suspend fun getMediaItemById(id: Long) = appDb.mediaItemDao().getById(id)


    suspend fun getActiveOnce() = appDb.activeMediaItemDao().getMediaItemOnce()
    suspend fun getPlaylistOnce() = appDb.globalPlaylistDao().getOnce()

/*    fun insertActiveItem(value: PlayerState){
        viewModelScope.launch(Dispatchers.IO){
            appDb.activeMediaItemDao().insert(value)
        }
    }

    fun updateActiveItem(value: PlayerState){
        viewModelScope.launch(Dispatchers.IO){
            appDb.activeMediaItemDao().update(value)
        }
    }

    fun updateActiveItemProgress(value: Long){
        viewModelScope.launch(Dispatchers.IO){
            appDb.activeMediaItemDao().updateProgress(value)
        }
    }

    suspend fun getOnce(): PlayerState? {
        return appDb.activeMediaItemDao().getOnce()
    }*/
}

//TODO Move media scan functions to Service and use coroutine for queries