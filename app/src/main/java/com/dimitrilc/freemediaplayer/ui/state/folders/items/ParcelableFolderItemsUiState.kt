package com.dimitrilc.freemediaplayer.ui.state.folders.items

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelableFolderItemsUiState(
    val title: String,
    val album: String,
    val thumbnailUri: String?,
    val videoId: Long?) : Parcelable {

        fun toState(
            thumbnailLoader: CustomBiFunction<String?,
                Long?, Bitmap?>, onClick: CustomIntConsumer
        ): FolderItemsUiState {
            return FolderItemsUiState(
                title = title,
                album = album,
                thumbnailUri = thumbnailUri,
                videoId = videoId,
                onClick = onClick,
                thumbnailLoader = thumbnailLoader
            )
        }

    }