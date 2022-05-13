package com.dimitrilc.freemediaplayer.ui.state

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_NONE

data class AudioPlayerUiState(
    val title: String? = "null",
    val album: String? = "null",
    val thumbnail: Bitmap? = null,
    val duration: Int = 0,
    val position: Int = 0,
    val isPlaying: Boolean = false,
    val repeatMode: Int = REPEAT_MODE_NONE
)