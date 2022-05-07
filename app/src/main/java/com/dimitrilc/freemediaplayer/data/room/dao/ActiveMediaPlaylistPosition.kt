package com.dimitrilc.freemediaplayer.data.room.dao

import androidx.room.ColumnInfo

data class ActiveMediaPlaylistPosition(
    @ColumnInfo(name = "active_media_id")
    val activeMediaId: Int = 1,
    @ColumnInfo(name = "global_playlist_position")
    val globalPlaylistPosition: Long,
)
