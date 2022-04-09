package com.dimitrilc.freemediaplayer.data.repos

import com.dimitrilc.freemediaplayer.data.source.room.activemedia.ActiveMediaRoomDataSource
import com.dimitrilc.freemediaplayer.data.repos.activemedia.ActiveMediaRepositoryImpl
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
class ActiveMediaRepositoryImplTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var activeMediaRoomDataSource: ActiveMediaRoomDataSource

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun creationTest(){
        val activeMediaRepositoryImpl = ActiveMediaRepositoryImpl(activeMediaRoomDataSource)
        assertNotNull(activeMediaRepositoryImpl)
    }
}