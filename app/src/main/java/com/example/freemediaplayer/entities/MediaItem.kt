package com.example.freemediaplayer.entities

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.freemediaplayer.room.Converters

@Entity(tableName = "media_items")
@TypeConverters(Converters::class)
data class MediaItem(
    @PrimaryKey val id: Long, //This is the same as MediaStore ID
    val uri: Uri, //Use this if Q or higher to access files
    val data: String, //Same as MediaStore.Video.Media.DATA
    val displayName: String, //parses from DATA
    val title: String,
    val isAudio: Boolean, //determines from MediaStore.AUDIO or VIDEO

    //for API 29 and lower, parse MediaStore.Audio.Media.DATA.
    // API 29 or higher, same as MediaStore.Audio.Media.RELATIVE_PATH.
    // Optionally, can just parse DATA for both variants.
    val location: String,
    val album: String = "Unknown Album",
    val albumId: Int,
    val albumArtUri: String? //Q or above. Same as uri. Below Q, query album_art in deprecated DB.
)