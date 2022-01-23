package com.example.freemediaplayer.entities

import androidx.room.Entity

@Entity(tableName = "video", primaryKeys = ["displayName", "uri"])
data class Video(
    val displayName: String,
    val uri: String
)