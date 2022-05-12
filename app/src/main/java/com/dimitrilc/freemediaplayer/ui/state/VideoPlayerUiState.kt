package com.dimitrilc.freemediaplayer.ui.state

import android.net.Uri
import android.support.v4.media.session.PlaybackStateCompat

data class VideoPlayerUiState(
    val title: String? = "null",
    val album: String? = "null",
    val uri: Uri = Uri.EMPTY,
    val duration: Int = 1,
    val position: Int = 0,
    val isPlaying: Boolean = false,
    val repeatMode: Int = PlaybackStateCompat.REPEAT_MODE_NONE,
    val areControlsVisible: Boolean = false
)
