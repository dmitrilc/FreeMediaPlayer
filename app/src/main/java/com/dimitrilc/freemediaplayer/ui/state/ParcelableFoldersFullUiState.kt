package com.dimitrilc.freemediaplayer.ui.state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

//Work around for inability to parcelize function types
@Parcelize
data class ParcelableFoldersFullUiState(val path: String,
                val isExpanded: Boolean,
                val relativePaths: List<String>): Parcelable {
    fun toState(
        foldersFullOnClick: (Int)->Unit,
        foldersRelativeOnClick: (fullPathPos: Int, bindingAdapterPos: Int)->Unit): FoldersFullUiState {
            return FoldersFullUiState(
                path = path,
                isExpanded = isExpanded,
                relativePath = FoldersRelativeUiState(
                    path = relativePaths,
                    onClick = foldersRelativeOnClick
                ),
                onClick = foldersFullOnClick
            )
    }
}