package com.dimitrilc.freemediaplayer.data.room.dao

import androidx.room.ColumnInfo

data class ActiveMediaProgress(
    @ColumnInfo(name = "active_media_id")
    val activeMediaId: Int = 1,
    val progress: Long
)
