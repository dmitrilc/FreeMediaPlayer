package com.example.freemediaplayer.entities

import androidx.room.Entity

@Entity(tableName = "audio", primaryKeys = ["displayName", "uri", "type"])
data class Audio(
    val displayName: String,
    val title: String?,
    val artist: String?,
    val album: String?,
    val uri: String,
    val type: String = "Unknown"
)