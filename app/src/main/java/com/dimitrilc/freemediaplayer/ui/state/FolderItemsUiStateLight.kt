package com.dimitrilc.freemediaplayer.ui.state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FolderItemsUiStateLight(
    val title: String,
    val album: String,
    val thumbnailUri: String?,
    val videoId: Long?
    ) : Parcelable