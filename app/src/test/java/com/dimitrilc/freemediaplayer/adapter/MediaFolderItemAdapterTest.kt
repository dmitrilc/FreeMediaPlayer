package com.dimitrilc.freemediaplayer.adapter

import com.dimitrilc.freemediaplayer.entities.MediaItem
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