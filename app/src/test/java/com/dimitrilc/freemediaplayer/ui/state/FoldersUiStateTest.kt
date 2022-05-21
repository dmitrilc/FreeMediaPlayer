package com.dimitrilc.freemediaplayer.ui.state

import org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FoldersUiStateTest {

    @Test
    fun testToString() {
        val parentPath = "parent"
        val isExpanded = false
        val relativePaths = listOf<String>()

        val foldersUiState = FoldersUiState(
            parentPath = parentPath,
            isExpanded = isExpanded,
            relativePaths = relativePaths
        )

        val sampleString = "FoldersUiState(parentPath=parent, isExpanded=false, relativePaths=[])"

        assertEquals("$foldersUiState", sampleString)
    }

    @Test
    fun testHashCode() {
        val parentPath = "parent"
        val isExpanded = false
        val relativePaths = listOf<String>()

        val foldersUiState = FoldersUiState(
            parentPath = parentPath,
            isExpanded = isExpanded,
            relativePaths = relativePaths
        )

        val sampleHash = 1175160363

        assertEquals(foldersUiState.hashCode(), sampleHash)
    }

    @Test
    fun testEquals() {
        val parentPath = "parent"
        val isExpanded = false
        val relativePaths = listOf<String>()

        val foldersUiState = FoldersUiState(
            parentPath = parentPath,
            isExpanded = isExpanded,
            relativePaths = relativePaths
        )

        val foldersUiState2 = FoldersUiState(
            parentPath = parentPath,
            isExpanded = isExpanded,
            relativePaths = relativePaths
        )

        assertEquals(foldersUiState, foldersUiState2)
    }
}