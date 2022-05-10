package com.dimitrilc.freemediaplayer.data.entities

import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_NONE
import androidx.room.*

const val ACTIVE_MEDIA_PROGRESS_KEY = "0"

@Entity(
    tableName = "active_media",
    foreignKeys = [
        ForeignKey(
            entity= MediaItem::class,
            parentColumns=["media_item_id"],
            childColumns=["media_item_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity= GlobalPlaylistItem::class,
            parentColumns=["global_playlist_item_id"],
            childColumns=["global_playlist_position"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index(value = ["media_item_id"]),
        Index(value = ["global_playlist_position"]),
    ]
)
data class ActiveMedia(
    @PrimaryKey
    @ColumnInfo(name = "active_media_id")
    val activeMediaId: Int = 1,
    @ColumnInfo(name = "global_playlist_position")
    val globalPlaylistPosition: Long,
    @ColumnInfo(name = "media_item_id")
    val mediaItemId: Long,
    val duration: Long = 0,
    val progress: Long = 0,
    @ColumnInfo(name = "is_playing")
    val isPlaying: Boolean = false,
    @ColumnInfo(name = "repeat_mode")
    val repeatMode: Int = REPEAT_MODE_NONE
)