package com.dimitrilc.freemediaplayer.ui.state

import com.dimitrilc.freemediaplayer.ui.state.folders.items.FolderItemsUiState
import org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FolderItemsUiStateTest {

    @Test
    fun testToString() {
        val title = "title"
        val album = "album"
        val thumbnail = null

        val folderItemsUiState = FolderItemsUiState(
            title = title,
            album = album,
            thumbnail = thumbnail
        )

        val sampleString = "FolderItemsUiState(title=title, album=album, thumbnail=null)"

        assertEquals("$folderItemsUiState", sampleString)
    }

    @Test
    fun testHashCode() {
        val title = "title"
        val album = "album"
        val thumbnail = null

        val folderItemsUiState = FolderItemsUiState(
            title = title,
            album = album,
            thumbnail = thumbnail
        )

        val sampleHash = 1572551625

        assertEquals(folderItemsUiState.hashCode(), sampleHash)
    }

    @Test
    fun testEquals() {
        val title = "title"
        val album = "album"
        val thumbnail = null

        val folderItemsUiState = FolderItemsUiState(
            title = title,
            album = album,
            thumbnail = thumbnail
        )

        val folderItemsUiState2 = FolderItemsUiState(
            title = title,
            album = album,
            thumbnail = thumbnail
        )

        assertEquals(folderItemsUiState, folderItemsUiState2)
    }
}