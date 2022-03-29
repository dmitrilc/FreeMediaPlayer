package com.dimitrilc.freemediaplayer.data.entities

import org.junit.Assert.*

import org.junit.Test

class GlobalPlaylistItemTest {

    @Test
    fun getMId() {
        val mId = 0L
        val mediaItemId = 1L

        val globalPlaylistItem = GlobalPlaylistItem(
            mId = mId,
            mediaItemId = mediaItemId
        )

        assertEquals(globalPlaylistItem.mId, mId)
    }

    @Test
    fun getMediaItemId() {
        val mId = 0L
        val mediaItemId = 1L

        val globalPlaylistItem = GlobalPlaylistItem(
            mId = mId,
            mediaItemId = mediaItemId
        )

        assertEquals(globalPlaylistItem.mediaItemId, mediaItemId)
    }

    @Test
    operator fun component1() {
        val mId = 0L
        val mediaItemId = 1L

        val globalPlaylistItem = GlobalPlaylistItem(
            mId = mId,
            mediaItemId = mediaItemId
        )

        assertEquals(globalPlaylistItem.component1(), mId)
    }

    @Test
    operator fun component2() {
        val mId = 0L
        val mediaItemId = 1L

        val globalPlaylistItem = GlobalPlaylistItem(
            mId = mId,
            mediaItemId = mediaItemId
        )

        assertEquals(globalPlaylistItem.component2(), mediaItemId)
    }

    @Test
    fun copy() {
        val mId = 0L
        val mediaItemId = 1L

        val globalPlaylistItem = GlobalPlaylistItem(
            mId = mId,
            mediaItemId = mediaItemId
        )

        val copy = globalPlaylistItem.copy()

        assertEquals(globalPlaylistItem, copy)
    }

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