package com.dimitrilc.freemediaplayer

import android.Manifest
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import java.io.FileNotFoundException

private val TAG = "DB_DEBUG"

fun isSameOrAfterS() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
fun isSameOrAfterQ() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
fun isBeforeQ() = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q

fun Application.getThumbnail(artUri: String?, videoId: Long?): Bitmap? {
    fun getVideoThumbBeforeQ(videoId: Long): Bitmap? {
        return MediaStore.Video.Thumbnails.getThumbnail(
            contentResolver,
            videoId,
            MediaStore.Video.Thumbnails.MINI_KIND,
            null
        )
    }

    var thumbnail: Bitmap? = null

    if (isSameOrAfterQ()) {
        try {
            thumbnail = contentResolver.loadThumbnail(
                Uri.parse(artUri),
                Size(300, 300),
                null
            )
        } catch (e: FileNotFoundException) {
        }
    } else {
        thumbnail = if (videoId == null) {
            BitmapFactory.decodeFile(artUri)
        } else {
            getVideoThumbBeforeQ(videoId)
        }
    }

    return thumbnail
}