package com.example.freemediaplayer.entities

import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.IntRange
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/*
@Entity(tableName = "player_state", foreignKeys = [
     ForeignKey(
          entity=MediaItem::class,
          parentColumns=["id"],
          childColumns=["mediaItemId"],
          onDelete = ForeignKey.CASCADE,
          onUpdate = ForeignKey.CASCADE,
     )
])
data class PlayerState(
     @PrimaryKey val mId: Int = 1,
     val mediaItemId: Long,
     val globalPlaylistPosition: Int,
     val maxDuration: Long = 0,
     val progress: Long = 0,
     @IntRange(
          from = PlaybackStateCompat.STATE_NONE.toLong(),
          to = PlaybackStateCompat.STATE_SKIPPING_TO_QUEUE_ITEM.toLong()
     )
     val state: Int = PlaybackStateCompat.STATE_NONE,
     @IntRange(
          from = PlaybackStateCompat.REPEAT_MODE_NONE.toLong(),
          to = PlaybackStateCompat.REPEAT_MODE_GROUP.toLong()
     )
     val repeatMode: Int = PlaybackStateCompat.REPEAT_MODE_NONE,
     @IntRange(
          from = PlaybackStateCompat.SHUFFLE_MODE_NONE.toLong(),
          to = PlaybackStateCompat.SHUFFLE_MODE_GROUP.toLong()
     )
     val shuffleMode: Int = PlaybackStateCompat.SHUFFLE_MODE_NONE
)*/
