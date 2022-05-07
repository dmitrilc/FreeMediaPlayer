package com.dimitrilc.freemediaplayer.data.room.dao

import androidx.room.ColumnInfo

data class ActiveMediaIsPlaying(
    @ColumnInfo(name = "active_media_id")
    val activeMediaId: Int = 1,
    @ColumnInfo(name = "is_playing")
    val isPlaying: Boolean
)
