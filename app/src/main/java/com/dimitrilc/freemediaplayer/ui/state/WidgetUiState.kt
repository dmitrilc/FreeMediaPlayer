package com.dimitrilc.freemediaplayer.ui.state

import android.graphics.Bitmap

data class WidgetUiState(
    val title: String?,
    val artist: String?,
    val isPlaying: Boolean,
    val thumb: Bitmap?
)