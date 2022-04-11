package com.dimitrilc.freemediaplayer.ui.state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FoldersUiState(
    val parentPath: String,
    val isExpanded: Boolean = false,
    val relativePaths: List<String>
) : Parcelable
