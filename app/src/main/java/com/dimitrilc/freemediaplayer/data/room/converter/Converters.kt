package com.dimitrilc.freemediaplayer.data.room.converter

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun stringToUri(string: String?): Uri? = string?.toUri()

    @TypeConverter
    fun uriToString(uri: Uri?): String? = uri?.toString()
}