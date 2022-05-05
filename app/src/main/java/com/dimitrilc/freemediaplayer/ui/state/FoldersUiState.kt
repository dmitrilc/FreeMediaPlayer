package com.dimitrilc.freemediaplayer.ui.state

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