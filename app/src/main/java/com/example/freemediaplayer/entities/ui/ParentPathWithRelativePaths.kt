package com.example.freemediaplayer.entities.ui

import androidx.room.Embedded
import androidx.room.Relation

data class ParentPathWithRelativePaths(
    @Embedded val parentPath: ParentPath,
    @Relation(
        parentColumn = "id",
        entityColumn = "parentPathId"
    )
    val relativePaths: List<RelativePath>
)
