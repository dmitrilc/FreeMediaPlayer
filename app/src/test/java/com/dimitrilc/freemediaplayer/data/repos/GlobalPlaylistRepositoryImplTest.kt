package com.dimitrilc.freemediaplayer.data.repos

import com.dimitrilc.freemediaplayer.data.datasources.globalplaylist.GlobalPlaylistRoomDataSource
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
    lateinit var globalPlaylistRoomDataSource: GlobalPlaylistRoomDataSource

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun creationTest(){
        val globalPlaylistRepositoryImpl = GlobalPlaylistRepositoryImpl(globalPlaylistRoomDataSource)
        assertNotNull(globalPlaylistRepositoryImpl)
    }
}