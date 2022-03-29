package com.dimitrilc.freemediaplayer.data.entities

import org.junit.Assert.*

import org.junit.Test

class GlobalPlaylistItemTest {

    @Test
    fun testToString() {
        val mId = 0L
        val mediaItemId = 1L

        val globalPlaylistItem = GlobalPlaylistItem(
            mId = mId,
            mediaItemId = mediaItemId
        )

        val sampleString = "GlobalPlaylistItem(mId=0, mediaItemId=1)"

        assertEquals(globalPlaylistItem.toString(), sampleString)
    }

    @Test
    fun testHashCode() {
        val mId = 0L
        val mediaItemId = 1L

        val globalPlaylistItem = GlobalPlaylistItem(
            mId = mId,
            mediaItemId = mediaItemId
        )

        val sampleHash = 1

        assertEquals(globalPlaylistItem.hashCode(), sampleHash)
    }

    @Test
    fun testEquals() {
        val mId = 0L
        val mediaItemId = 1L

        val globalPlaylistItem = GlobalPlaylistItem(
            mId = mId,
            mediaItemId = mediaItemId
        )

        val globalPlaylistItem2 = GlobalPlaylistItem(
            mId = mId,
            mediaItemId = mediaItemId
        )

        assertEquals(globalPlaylistItem, globalPlaylistItem2)
    }

}