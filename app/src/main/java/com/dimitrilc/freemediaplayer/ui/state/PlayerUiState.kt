package com.dimitrilc.freemediaplayer.ui.state

import android.graphics.Bitmap
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_NONE

data class PlayerUiState(
    val title: String? = "null",
    val album: String? = "null",
    val thumbnail: Bitmap? = null,
    val duration: Long = 0,
    val position: Long = 0,
    val isPlaying: Boolean = false,
    val repeatMode: Int = REPEAT_MODE_NONE
)