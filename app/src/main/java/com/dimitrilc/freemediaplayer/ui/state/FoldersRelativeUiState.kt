package com.dimitrilc.freemediaplayer.ui.state

data class FoldersRelativeUiState(
    val path: List<String>,
    val onClick: (fullPathPos: Int, bindingAdapterPos: Int)->Unit
)