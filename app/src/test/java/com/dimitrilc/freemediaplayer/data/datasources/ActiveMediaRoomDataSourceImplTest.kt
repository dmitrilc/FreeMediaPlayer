package com.dimitrilc.freemediaplayer.data.datasources

import com.dimitrilc.freemediaplayer.data.source.room.activemedia.ActiveMediaRoomDataSourceImpl
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaDao
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
class ActiveMediaRoomDataSourceImplTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var activeMediaDao: ActiveMediaDao

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun creationTest(){
        val activeMediaRoomDataSourceImpl = ActiveMediaRoomDataSourceImpl(activeMediaDao)
        assertNotNull(activeMediaRoomDataSourceImpl)
    }

}