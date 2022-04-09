package com.dimitrilc.freemediaplayer.data.datasources

import com.dimitrilc.freemediaplayer.data.source.room.globalplaylist.GlobalPlaylistRoomDataSourceImpl
import com.dimitrilc.freemediaplayer.data.room.dao.GlobalPlaylistDao
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
class GlobalPlaylistRoomDataSourceImplTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var globalPlaylistDao: GlobalPlaylistDao

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun creationTest(){
        val globalPlaylistRoomDataSourceImpl = GlobalPlaylistRoomDataSourceImpl(globalPlaylistDao)
        assertNotNull(globalPlaylistRoomDataSourceImpl)
    }
}