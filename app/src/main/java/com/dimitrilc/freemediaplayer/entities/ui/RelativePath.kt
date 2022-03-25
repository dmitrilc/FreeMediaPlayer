package com.dimitrilc.freemediaplayer.entities.ui

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(primaryKeys = ["parentPathId", "relativePath"])
data class RelativePath(
    val parentPathId: Long,
    val relativePath: String
)