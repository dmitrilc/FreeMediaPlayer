package com.dimitrilc.freemediaplayer.data.datasources

import android.content.Context
import com.dimitrilc.freemediaplayer.data.room.dao.MediaItemDao
import dagger.hilt.android.qualifiers.ApplicationContext
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
class MediaStoreDataSourceImplTest{

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    @ApplicationContext
    lateinit var appContext: Context

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun creationTest(){
        val mediaStoreDataSourceImpl = MediaStoreDataSourceImpl(appContext)
        assertNotNull(mediaStoreDataSourceImpl)
    }

}