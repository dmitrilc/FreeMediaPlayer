package com.dimitrilc.freemediaplayer.ui.adapter

import org.junit.Assert.*

import org.junit.Test

class FoldersRelativeAdapterTest {

    @Test
    fun getItemCount() {
        val dataSet = emptyList<String>()
        val foldersRelativeAdapter = FoldersRelativeAdapter(dataSet, 0)

        assertEquals(dataSet.size, foldersRelativeAdapter.itemCount)
    }
}