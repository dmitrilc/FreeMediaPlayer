package com.dimitrilc.freemediaplayer.data.entities

import org.junit.Assert.*

import org.junit.Test

class ActiveMediaTest {

    @Test
    fun getMId() {
        val mId = 1
        val globalPlaylistPosition = 0L
        val mediaItemId = 1L

        val activeMedia = ActiveMedia(
            globalPlaylistPosition = globalPlaylistPosition,
            mediaItemId = mediaItemId
        )

        assertEquals(activeMedia.mId, mId)
    }

    @Test
    fun getGlobalPlaylistPosition() {
        val mId = 1
        val globalPlaylistPosition = 0L
        val mediaItemId = 1L

        val activeMedia = ActiveMedia(
            globalPlaylistPosition = globalPlaylistPosition,
            mediaItemId = mediaItemId
        )

        assertEquals(activeMedia.globalPlaylistPosition, globalPlaylistPosition)
    }

    @Test
    fun getMediaItemId() {
        val mId = 1
        val globalPlaylistPosition = 0L
        val mediaItemId = 1L

        val activeMedia = ActiveMedia(
            globalPlaylistPosition = globalPlaylistPosition,
            mediaItemId = mediaItemId
        )

        assertEquals(activeMedia.mediaItemId , mediaItemId)
    }

    @Test
    operator fun component1() {
        val mId = 1
        val globalPlaylistPosition = 0L
        val mediaItemId = 1L

        val activeMedia = ActiveMedia(
            globalPlaylistPosition = globalPlaylistPosition,
            mediaItemId = mediaItemId
        )

        assertEquals(activeMedia.component1() , mId)
    }

    @Test
    operator fun component2() {
        val mId = 1
        val globalPlaylistPosition = 0L
        val mediaItemId = 1L

        val activeMedia = ActiveMedia(
            globalPlaylistPosition = globalPlaylistPosition,
            mediaItemId = mediaItemId
        )

        assertEquals(activeMedia.component2() , globalPlaylistPosition)
    }

    @Test
    operator fun component3() {
        val mId = 1
        val globalPlaylistPosition = 0L
        val mediaItemId = 1L

        val activeMedia = ActiveMedia(
            globalPlaylistPosition = globalPlaylistPosition,
            mediaItemId = mediaItemId
        )

        assertEquals(activeMedia.component3() , mediaItemId)
    }

    @Test
    fun copy() {
        val mId = 1
        val globalPlaylistPosition = 0L
        val mediaItemId = 1L

        val activeMedia = ActiveMedia(
            globalPlaylistPosition = globalPlaylistPosition,
            mediaItemId = mediaItemId
        )

        val copy = activeMedia.copy()

        assertEquals(activeMedia , copy)
    }

    @Test
    fun testToString() {
        val mId = 1
        val globalPlaylistPosition = 0L
        val mediaItemId = 1L

        val activeMedia = ActiveMedia(
            globalPlaylistPosition = globalPlaylistPosition,
            mediaItemId = mediaItemId
        )

        val sampleToString = "ActiveMedia(mId=1, globalPlaylistPosition=0, mediaItemId=1)"

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

        val sampleHash = 962

        assertEquals(activeMedia.hashCode(), 962)
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