package com.dimitrilc.freemediaplayer.hilt

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.dimitrilc.freemediaplayer.data.proto.bottomNavProtoDataStore
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BottomNavProtoDataStoreModuleTest {

    @Test
    fun provideBottomNavProtoDataStore() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val bottomNavProtoDataStore = BottomNavProtoDataStoreModule.provideBottomNavProtoDataStore(context)
        assertEquals(bottomNavProtoDataStore, context.bottomNavProtoDataStore)
    }
}