package com.dimitrilc.freemediaplayer.hilt.repos

import com.dimitrilc.freemediaplayer.data.repos.MediaManager
import com.dimitrilc.freemediaplayer.data.repos.MediaManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MediaManagerModule {
    @Binds
    abstract fun bindMediaManager(mediaManager: MediaManagerImpl): MediaManager
}