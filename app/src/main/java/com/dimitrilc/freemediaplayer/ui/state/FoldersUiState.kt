package com.dimitrilc.freemediaplayer.ui.state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class FoldersUiState(
    val fullFolders: List<FoldersFullUiState>
) {
    //Remove all function types so the state can be parcelized.
    fun toParcelable(): List<ParcelableFoldersFullUiState>{
        return fullFolders.map {
            ParcelableFoldersFullUiState(
                path = it.path,
                isExpanded = it.isExpanded,
                relativePaths = it.relativePath.path
            )
        }.toList()
    }
}

data class FoldersFullUiState(
    val path: String,
    val isExpanded: Boolean = false,
    val relativePath: FoldersRelativeUiState,
    val onClick: (bindingAdapterPos: Int)->Unit
)

data class FoldersRelativeUiState(
    val path: List<String>,
    val onClick: (fullPathPos: Int, bindingAdapterPos: Int)->Unit
)

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
                    path =relativePaths,
                    onClick = foldersRelativeOnClick
                ),
                onClick = foldersFullOnClick
            )
    }
}