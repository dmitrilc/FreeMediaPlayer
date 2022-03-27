package com.dimitrilc.freemediaplayer.ui.state

data class FoldersUiState(
    val parentPath: String,
    val isExpanded: Boolean = false,
    val relativePaths: List<String>
)
