package com.dimitrilc.freemediaplayer.data.repos

import com.dimitrilc.freemediaplayer.data.datasources.MediaStoreDataSource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import javax.inject.Inject

@RunWith(RobolectricTestRunner::class)
@HiltAndroidTest
class MediaStoreRepositoryImplTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var mediaStoreDataSource: MediaStoreDataSource

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun creationTest(){
        val mediaStoreRepositoryImpl = MediaStoreRepositoryImpl(mediaStoreDataSource)
        Assert.assertNotNull(mediaStoreRepositoryImpl)
    }
}