package com.dimitrilc.freemediaplayer.data.room.converter

import android.net.Uri
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private const val SAMPLE_STRING = "1"

@RunWith(RobolectricTestRunner::class)
class ConvertersTest {

    @Test
    fun stringToUriNotNull() {
        val converters = Converters()
        val convertedUri = converters.stringToUri(SAMPLE_STRING)
        val uri = Uri.parse(SAMPLE_STRING)
        assertEquals(convertedUri, uri)
    }

    @Test
    fun stringToUriNull(){
        val converters = Converters()
        val uri = converters.stringToUri(null)
        assertNull(uri)
    }

    @Test
    fun uriToStringNotNull() {
        val converters = Converters()
        val sampleUri = Uri.parse(SAMPLE_STRING)
        val string = converters.uriToString(sampleUri)
        assertEquals(string, SAMPLE_STRING)
    }

    @Test
    fun uriToStringNull(){
        val converters = Converters()
        val string = converters.uriToString(null)
        assertNull(string)
    }
}