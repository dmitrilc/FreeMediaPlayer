package com.dimitrilc.freemediaplayer.ui.adapter

import com.dimitrilc.freemediaplayer.ui.state.FolderItemsUiState
import org.junit.Assert.*

import org.junit.Test

class MediaFolderItemAdapterTest {

    @Test
    fun getItemCount() {
        val dataSet = emptyList<FolderItemsUiState>()
        val mediaFolderItemAdapter = MediaFolderItemAdapter(dataSet)

        assertEquals(dataSet.size, mediaFolderItemAdapter.itemCount)
    }
}