package com.dimitrilc.freemediaplayer.ui.state

data class FoldersFullUiState(
    val path: String,
    val isExpanded: Boolean = false,
    val relativePath: FoldersRelativeUiState,
    val onClick: (bindingAdapterPos: Int)->Unit
)