package com.example.freemediaplayer.entities

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.freemediaplayer.room.Converters

@Entity(tableName = "video")
@TypeConverters(Converters::class)
data class Video(
    @PrimaryKey val id: Long, //This is the same as MediaStore ID
    val uri: Uri, //Use this if Q or higher to access files
    val data: String, //Same as MediaStore.Video.Media.DATA
    val displayName: String, //parses from DATA
    val title: String,
    val album: String = "Unknown Album",

    //for API 29 and lower, parse MediaStore.Audio.Media.DATA.
    // API 29 or higher, same as MediaStore.Audio.Media.RELATIVE_PATH.
    // Optionally, can just parse DATA for both variants.
    val location: String
)