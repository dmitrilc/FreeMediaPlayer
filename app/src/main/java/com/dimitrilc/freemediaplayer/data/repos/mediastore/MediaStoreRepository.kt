package com.dimitrilc.freemediaplayer.data.repos.mediastore

import android.graphics.Bitmap
import com.dimitrilc.freemediaplayer.data.entities.MediaItem

interface MediaStoreRepository {
    fun queryAudios(): List<MediaItem>?
    fun queryVideos(): List<MediaItem>?

    fun getAlbumArtUriBeforeQ(albumId: Int): String?

    fun getThumbnail(artUri: String?, videoId: Long?): Bitmap?
}