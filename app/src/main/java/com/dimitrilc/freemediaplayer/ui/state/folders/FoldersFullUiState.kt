package com.dimitrilc.freemediaplayer.ui.state.folders

import com.dimitrilc.freemediaplayer.ui.state.callback.BiIntConsumer
import com.dimitrilc.freemediaplayer.ui.state.callback.IntConsumerCompat

data class FoldersFullUiState(
    val path: String,
    val isExpanded: Boolean = false,
    val relativePaths: List<String>,
    val onFullClick: IntConsumerCompat,
    val onRelativeClick: BiIntConsumer
){
    fun toParcelable(): ParcelableFoldersFullUiState {
        return ParcelableFoldersFullUiState(
            path = path,
            isExpanded = isExpanded,
            relativePaths = relativePaths
        )
    }
}