package com.example.freemediaplayer.entities

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(tableName = "global_playlist", foreignKeys = [
    ForeignKey(
        entity=MediaItem::class,
        parentColumns=["id"],
        childColumns=["mediaItemId"],
        onDelete = CASCADE,
        onUpdate = CASCADE,
    )
])
data class GlobalPlaylistItem(
    @PrimaryKey val mId: Long,
    val mediaItemId: Long
)

//TODO Rename entity tables to use underscore to match SQL conventions