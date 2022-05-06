package com.dimitrilc.freemediaplayer.ui.state.folders

import android.os.Parcelable
import com.dimitrilc.freemediaplayer.ui.state.callback.BiIntConsumer
import com.dimitrilc.freemediaplayer.ui.state.callback.IntConsumerCompat
import kotlinx.parcelize.Parcelize

//Work around for inability to parcelize function types
@Parcelize
data class ParcelableFoldersFullUiState(
    val path: String,
    val isExpanded: Boolean,
    val relativePaths: List<String>): Parcelable {

    fun toState(
        foldersFullOnClick: IntConsumerCompat,
        foldersRelativeOnClick: BiIntConsumer
    ): FoldersFullUiState {
            return FoldersFullUiState(
                path = path,
                isExpanded = isExpanded,
                relativePaths = relativePaths,
                onFullClick = foldersFullOnClick,
                onRelativeClick = foldersRelativeOnClick
            )
    }
}