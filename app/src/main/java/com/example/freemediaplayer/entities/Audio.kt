package com.example.freemediaplayer.entities

import androidx.room.Entity

@Entity(tableName = "audio", primaryKeys = ["displayName", "type", "uri"])
data class Audio(
    val displayName: String, //parses from DATA
    val type: String, //for API 29 and lower, parse MediaStore.Audio.Media.DATA. API 29 or higher, same as MediaStore.Audio.Media.RELATIVE_PATH. Optionally, can just parse DATA for both variants.
    val uri: String //Same as MediaStore.Audio.Media.DATA
//    val title: String?,
//    val artist: String?,
//    val album: String?,
)