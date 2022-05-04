package com.dimitrilc.freemediaplayer.data.repos

import android.app.Application
import com.dimitrilc.freemediaplayer.data.source.room.globalplaylist.GlobalPlaylistLocalDataSource
import com.dimitrilc.freemediaplayer.data.repos.globalplaylist.GlobalPlaylistRepositoryImpl
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
class GlobalPlaylistRepositoryImplTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var globalPlaylistLocalDataSource: GlobalPlaylistLocalDataSource

    @Inject
    lateinit var app: Application

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun creationTest(){
        val globalPlaylistRepositoryImpl = GlobalPlaylistRepositoryImpl(globalPlaylistLocalDataSource, app)
        assertNotNull(globalPlaylistRepositoryImpl)
    }
}