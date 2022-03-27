package com.dimitrilc.freemediaplayer.ui.adapter

import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import org.junit.Assert.*

import org.junit.Test

class MediaFolderItemAdapterTest {

    @Test
    fun getItemCount() {
        val dataSet = emptyList<MediaItem>()
        val mediaFolderItemAdapter = MediaFolderItemAdapter(dataSet)

        assertEquals(dataSet.size, mediaFolderItemAdapter.itemCount)
    }
}