package com.dimitrilc.freemediaplayer.data.entities

import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_NONE
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem
import com.dimitrilc.freemediaplayer.data.entities.MediaItem

@Entity(tableName = "active_media", foreignKeys = [
    ForeignKey(
        entity= MediaItem::class,
        parentColumns=["id"],
        childColumns=["mediaItemId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE,
    ),
    ForeignKey(
        entity= GlobalPlaylistItem::class,
        parentColumns=["mId"],
        childColumns=["globalPlaylistPosition"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE,
    )
])
data class ActiveMedia(
    @PrimaryKey val mId: Int = 1,
    val globalPlaylistPosition: Long,
    val mediaItemId: Long,
    val duration: Long = 0,
    val progress: Long = 0,
    val isPlaying: Boolean = false,
    val repeatMode: Int = REPEAT_MODE_NONE
)