package com.dimitrilc.freemediaplayer.ui.adapter

import com.dimitrilc.freemediaplayer.ui.state.FoldersUiState
import org.junit.Assert.*

import org.junit.Test

class FoldersFullAdapterTest {

    @Test
    fun getItemCount() {
        val dataSet = emptyList<FoldersUiState>()
        val foldersFullAdapter = FoldersFullAdapter(dataSet){}

        assertEquals(dataSet.size, foldersFullAdapter.itemCount)
    }
}