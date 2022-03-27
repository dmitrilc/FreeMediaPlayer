package com.dimitrilc.freemediaplayer.ui.adapter

import org.junit.Assert.*

import org.junit.Test

class FoldersFullAdapterTest {

    @Test
    fun getItemCount() {
        val dataSet = emptyList<ParentPathWithRelativePaths>()
        val foldersFullAdapter = FoldersFullAdapter(dataSet){}

        assertEquals(dataSet.size, foldersFullAdapter.itemCount)
    }
}