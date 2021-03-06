package com.dimitrilc.freemediaplayer.data.repos

import android.app.Application
import com.dimitrilc.freemediaplayer.data.repos.activemedia.ActiveMediaRepository
import com.dimitrilc.freemediaplayer.data.repos.globalplaylist.GlobalPlaylistRepository
import com.dimitrilc.freemediaplayer.data.repos.mediaitem.MediaItemRepository
import com.dimitrilc.freemediaplayer.data.room.database.AppDatabase
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
class MediaManagerImplTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var app: Application

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun creationTest(){
        val mediaManagerImpl = MediaManagerImpl(app)
        assertNotNull(mediaManagerImpl)
    }
}