package com.example.freemediaplayer.viewmodel

import android.app.Application
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

    val audioBrowser = MutableLiveData<MediaBrowserCompat>()
    val globalPlaylist = appDb.globalPlaylistDao().getGlobalPlaylist()
    val activeMediaLiveData = appDb.activeMediaItemDao().getMediaItemLiveData()

    suspend fun getCurrentFolderFullPath() = appDb.folderItemsUiDao().getCurrentFolderItemsUi()

    fun getThumbnail(artUri: String, videoId: Long?): Bitmap? {
        var thumbnail: Bitmap? = null

        if (isSameOrAfterQ()) {
            try {
                thumbnail = app.contentResolver.loadThumbnail(
                    Uri.parse(artUri),
                    Size(300, 300),
                    null
                )
            } catch (e: FileNotFoundException) {
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

    suspend fun getActiveOnce() = appDb.activeMediaItemDao().getMediaItemOnce()
    suspend fun getPlaylistOnce() = appDb.globalPlaylistDao().getOnce()
}