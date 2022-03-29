package com.dimitrilc.freemediaplayer.data.repos

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
    lateinit var activeMediaRepository: ActiveMediaRepository

    @Inject
    lateinit var globalPlaylistRepository: GlobalPlaylistRepository

    @Inject
    lateinit var mediaItemRepository: MediaItemRepository

    @Inject
    lateinit var appDb: AppDatabase

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun creationTest(){
        val mediaManagerImpl = MediaManagerImpl(
            mediaItemRepository,
            globalPlaylistRepository,
            activeMediaRepository,
            appDb
        )
        assertNotNull(mediaManagerImpl)
    }
}