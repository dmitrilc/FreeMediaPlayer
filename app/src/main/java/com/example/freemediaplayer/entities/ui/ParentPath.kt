package com.example.freemediaplayer.entities.ui

import androidx.room.*

@Entity(
    indices = [Index(
        value = ["isAudio", "parentPath"],
        unique = true)
    ]
)
data class ParentPath(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val isAudio: Boolean,
    val parentPath: String,
    val isExpanded: Boolean = true
)