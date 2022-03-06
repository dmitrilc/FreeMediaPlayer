package com.example.freemediaplayer.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "global_playlist")
data class GlobalPlaylist(
    @PrimaryKey @Embedded val mediaItem: MediaItem
)