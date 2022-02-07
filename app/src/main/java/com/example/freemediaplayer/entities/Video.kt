package com.example.freemediaplayer.entities

import androidx.room.Entity

@Entity(tableName = "video", primaryKeys = ["displayName", "uri", "path"])
data class Video(
    val displayName: String,
    val uri: String,
    val path: String
)