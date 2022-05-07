package com.dimitrilc.freemediaplayer.data.entities

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(
    tableName = "global_playlist",
    foreignKeys = [
        ForeignKey(
            entity=MediaItem::class,
            parentColumns=["media_item_id"],
            childColumns=["media_item_id"],
            onDelete = CASCADE,
            onUpdate = CASCADE,
        )
    ],
    indices = [Index(value = ["global_playlist_item_id"])]
)
data class GlobalPlaylistItem(
    @PrimaryKey
    @ColumnInfo(name = "global_playlist_item_id")
    val globalPlaylistItemId: Long,
    @ColumnInfo(name = "media_item_id")
    val mediaItemId: Long
)