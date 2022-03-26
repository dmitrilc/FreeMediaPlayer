package com.dimitrilc.freemediaplayer.adapter

import com.dimitrilc.freemediaplayer.entities.ui.ParentPathWithRelativePaths
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