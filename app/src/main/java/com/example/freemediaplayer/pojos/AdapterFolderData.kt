package com.example.freemediaplayer.pojos

data class AdapterFolderData(
    val parentPath: String,
    val relativePaths: List<String>,
    val isCollapsed: Boolean = false
)