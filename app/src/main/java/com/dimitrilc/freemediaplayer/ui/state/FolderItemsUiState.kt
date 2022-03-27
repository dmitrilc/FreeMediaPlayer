package com.dimitrilc.freemediaplayer.ui.state

import android.graphics.Bitmap

data class FolderItemsUiState(
    val title: String,
    val album: String,
    val thumbnail: Bitmap?,
)
