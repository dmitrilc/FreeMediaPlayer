package com.dimitrilc.freemediaplayer.data.entities

import org.junit.Assert.*

import org.junit.Test

class ActiveMediaTest {

    @Test
    fun testToString() {
        val mId = 1
        val globalPlaylistPosition = 0L
        val mediaItemId = 1L

        val activeMedia = ActiveMedia(
            globalPlaylistPosition = globalPlaylistPosition,
            mediaItemId = mediaItemId
        )

        val sampleToString = "ActiveMedia(mId=1, globalPlaylistPosition=0, mediaItemId=1, duration=0, progress=0, isPlaying=false, repeatMode=0)"

        assertEquals(activeMedia.toString(), sampleToString)
    }

    @Test
    fun testHashCode() {
        val mId = 1
        val globalPlaylistPosition = 0L
        val mediaItemId = 1L

        val activeMedia = ActiveMedia(
            globalPlaylistPosition = globalPlaylistPosition,
            mediaItemId = mediaItemId
        )

        val sampleHash = 888427202

        assertEquals(activeMedia.hashCode(), sampleHash)
    }

    @Test
    fun testEquals() {
        val mId = 1
        val globalPlaylistPosition = 0L
        val mediaItemId = 1L

        val activeMedia = ActiveMedia(
            globalPlaylistPosition = globalPlaylistPosition,
            mediaItemId = mediaItemId
        )

        val activeMedia2 = ActiveMedia(
            globalPlaylistPosition = globalPlaylistPosition,
            mediaItemId = mediaItemId
        )

        assertEquals(activeMedia, activeMedia2)
    }

}