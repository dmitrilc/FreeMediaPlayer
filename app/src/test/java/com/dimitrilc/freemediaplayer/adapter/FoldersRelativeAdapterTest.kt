package com.dimitrilc.freemediaplayer.adapter

import com.dimitrilc.freemediaplayer.entities.ui.RelativePath
import org.junit.Assert.*

import org.junit.Test

class FoldersRelativeAdapterTest {

    @Test
    fun getItemCount() {
        val dataSet = emptyList<RelativePath>()
        val foldersRelativeAdapter = FoldersRelativeAdapter(dataSet, 0)

        assertEquals(dataSet.size, foldersRelativeAdapter.itemCount)
    }
}