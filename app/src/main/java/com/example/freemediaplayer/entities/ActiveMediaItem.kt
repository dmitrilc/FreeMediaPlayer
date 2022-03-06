package com.example.freemediaplayer.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ActiveMediaItem(
     @PrimaryKey @Embedded val mediaItem: MediaItem
)
