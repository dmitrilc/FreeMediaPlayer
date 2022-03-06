package com.example.freemediaplayer.entities.ui

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FolderItemsUi(
    @PrimaryKey val id: Int = 1,
    val fullPath: String
)
