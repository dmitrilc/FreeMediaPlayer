package com.dimitrilc.freemediaplayer.ui.state.folders.items

import android.graphics.Bitmap
import com.dimitrilc.freemediaplayer.ui.state.callback.CustomBiFunction
import com.dimitrilc.freemediaplayer.ui.state.callback.IntConsumerCompat

//https://youtrack.jetbrains.com/issue/KT-10671
data class FolderItemsUiState(
    val title: String,
    val album: String,
    val thumbnailUri: String?,
    val videoId: Long?,
    val thumbnailLoader: CustomBiFunction<String?, Long?, Bitmap?>,
    val onClick: IntConsumerCompat
){
    fun toParcel(): ParcelableFolderItemsUiState {
        return ParcelableFolderItemsUiState(
            title = title,
            album = album,
            thumbnailUri = thumbnailUri,
            videoId = videoId
        )
    }

    companion object {
        fun empty() = FolderItemsUiState(
            title = "",
            album = "",
            thumbnailUri = null,
            videoId = null,
            thumbnailLoader = object : CustomBiFunction<String?, Long?, Bitmap?>{
                override fun invoke(arg1: String?, arg2: Long?): Bitmap? = null
            },
            onClick = object : IntConsumerCompat {
                override fun invoke(arg1: Int) {}
            }
        )
    }
}