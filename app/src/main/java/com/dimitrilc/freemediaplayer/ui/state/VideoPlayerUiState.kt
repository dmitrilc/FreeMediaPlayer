package com.dimitrilc.freemediaplayer.ui.state

import android.support.v4.media.session.PlaybackStateCompat

data class VideoPlayerUiState(
    val title: String? = "null",
    val album: String? = "null",
    val duration: Long = 0,
    val position: Long = 0,
    val isPlaying: Boolean = false,
    val repeatMode: Int = PlaybackStateCompat.REPEAT_MODE_NONE
)
