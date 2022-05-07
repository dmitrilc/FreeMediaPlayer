package com.dimitrilc.freemediaplayer.data.entities

import android.net.Uri
import androidx.room.*
import com.dimitrilc.freemediaplayer.data.room.converter.Converters

@Entity(tableName = "media_item")
@TypeConverters(Converters::class)
data class MediaItem(
    @PrimaryKey
    @ColumnInfo(name = "media_item_id")
    val mediaItemId: Long, //This is the same as MediaStore ID
    val uri: Uri, //Use this if Q or higher to access files
    val data: String, //Same as MediaStore.Video.Media.DATA
    @ColumnInfo(name = "display_name")
    val displayName: String, //parses from DATA
    val title: String,
    @ColumnInfo(name = "is_audio")
    val isAudio: Boolean, //determines from MediaStore.AUDIO or VIDEO

    //for API 29 and lower, parse MediaStore.Audio.Media.DATA.
    // API 29 or higher, same as MediaStore.Audio.Media.RELATIVE_PATH.
    // Optionally, can just parse DATA for both variants.
    val location: String,
    val album: String = "Unknown Album",
    @ColumnInfo(name = "album_id")
    val albumId: Int,
    @ColumnInfo(name = "album_art_uri")
    val albumArtUri: String? //Q or above. Same as uri. Below Q, query album_art in deprecated DB.
)