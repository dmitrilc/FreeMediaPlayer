package com.example.freemediaplayer.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "active_item", foreignKeys = [
    ForeignKey(
        entity=MediaItem::class,
        parentColumns=["id"],
        childColumns=["mediaItemId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE,
    ),
    ForeignKey(
        entity=GlobalPlaylistItem::class,
        parentColumns=["mId"],
        childColumns=["globalPlaylistPosition"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE,
    )
])
data class ActiveMediaItem(
    @PrimaryKey val mId: Int = 1,
    val globalPlaylistPosition: Long,
    val mediaItemId: Long
)