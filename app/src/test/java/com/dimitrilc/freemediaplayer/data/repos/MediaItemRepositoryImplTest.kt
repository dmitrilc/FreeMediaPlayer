package com.dimitrilc.freemediaplayer.data.repos

import com.dimitrilc.freemediaplayer.data.source.room.mediaitem.MediaItemLocalDataSource
import com.dimitrilc.freemediaplayer.data.repos.mediaitem.MediaItemRepositoryImpl
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import javax.inject.Inject

@RunWith(RobolectricTestRunner::class)
@HiltAndroidTest
class MediaItemRepositoryImplTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var mediaItemLocalDataSource: MediaItemLocalDataSource

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun creationTest(){
        val mediaItemRepositoryImpl = MediaItemRepositoryImpl(mediaItemLocalDataSource)
        assertNotNull(mediaItemRepositoryImpl)
    }
}