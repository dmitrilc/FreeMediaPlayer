package com.dimitrilc.freemediaplayer.ui.state.folders.items

import android.graphics.Bitmap

//https://youtrack.jetbrains.com/issue/KT-10671
data class FolderItemsUiState(
    val title: String,
    val album: String,
    val thumbnailUri: String?,
    val videoId: Long?,
    val thumbnailLoader: CustomBiFunction<String?, Long?, Bitmap?>,
    val onClick: CustomIntConsumer
){
    fun toParcel(): ParcelableFolderItemsUiState {
        return ParcelableFolderItemsUiState(
            title = title,
            album = album,
            thumbnailUri = thumbnailUri,
            videoId = videoId
        )
    }
}

//Because old Android cannot use functional interfaces
interface CustomIntConsumer {
    operator fun invoke(arg1: Int)
}

interface CustomBiFunction<in T, in U, out R> {
    operator fun invoke(arg1: T, arg2: U): R
}