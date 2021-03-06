package com.dimitrilc.freemediaplayer.hilt

import android.content.Context
import com.dimitrilc.freemediaplayer.data.proto.bottomNavProtoDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

private const val TAG = "BottomNavHilt"

@Module
@InstallIn(ViewModelComponent::class)
object BottomNavProtoDataStoreModule {

    @Provides
    fun provideBottomNavProtoDataStore(@ApplicationContext context: Context) = context.bottomNavProtoDataStore

}