package com.dimitrilc.freemediaplayer.data.entities

import android.net.Uri
import org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MediaItemTest {

    @Test
    fun testToString() {
        val id = 1L
        val uri = Uri.parse("Uri")
        val data = "data"
        val displayName = "displayName"
        val title = "title"
        val isAudio = true
        val location = "location"
        val album = "album"
        val albumId = 1
        val albumArtUri = "albumArtUri"

        val mediaItem = MediaItem(
            id = id,
            uri = uri,
            data = data,
            displayName = displayName,
            title = title,
            isAudio = isAudio,
            location = location,
            album = album,
            albumId = albumId,
            albumArtUri = albumArtUri
        )

        val sampleString = "MediaItem(id=1, uri=Uri, data=data, displayName=displayName, title=title, isAudio=true, location=location, album=album, albumId=1, albumArtUri=albumArtUri)"

        assertEquals(mediaItem.toString(), sampleString)
    }

    @Test
    fun testHashCode() {
        val id = 1L
        val uri = Uri.parse("Uri")
        val data = "data"
        val displayName = "displayName"
        val title = "title"
        val isAudio = true
        val location = "location"
        val album = "album"
        val albumId = 1
        val albumArtUri = "albumArtUri"

        val mediaItem = MediaItem(
            id = id,
            uri = uri,
            data = data,
            displayName = displayName,
            title = title,
            isAudio = isAudio,
            location = location,
            album = album,
            albumId = albumId,
            albumArtUri = albumArtUri
        )

        val sampleHash = 1125974696

        assertEquals(mediaItem.hashCode(), sampleHash)
    }

    @Test
    fun testEquals() {
        val id = 1L
        val uri = Uri.parse("Uri")
        val data = "data"
        val displayName = "displayName"
        val title = "title"
        val isAudio = true
        val location = "location"
        val album = "album"
        val albumId = 1
        val albumArtUri = "albumArtUri"

        val mediaItem = MediaItem(
            id = id,
            uri = uri,
            data = data,
            displayName = displayName,
            title = title,
            isAudio = isAudio,
            location = location,
            album = album,
            albumId = albumId,
            albumArtUri = albumArtUri
        )

        val mediaItem2 = MediaItem(
            id = id,
            uri = uri,
            data = data,
            displayName = displayName,
            title = title,
            isAudio = isAudio,
            location = location,
            album = album,
            albumId = albumId,
            albumArtUri = albumArtUri
        )

        assertEquals(mediaItem, mediaItem2)
    }
}