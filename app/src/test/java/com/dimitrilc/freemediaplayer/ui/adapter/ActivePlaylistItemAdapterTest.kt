package com.dimitrilc.freemediaplayer.ui.adapter

import com.dimitrilc.freemediaplayer.data.entities.MediaItem
import org.junit.Assert.*
import org.junit.Test

class ActivePlaylistItemAdapterTest {

    @Test
    fun getItemCount() {
        val dataset = mutableListOf<MediaItem>()
        val activePlaylistItemAdapter = ActivePlaylistItemAdapter(dataset)
        assertEquals(dataset.size, activePlaylistItemAdapter.itemCount)
    }
}