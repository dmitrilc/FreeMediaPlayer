package com.dimitrilc.freemediaplayer.hilt.repos

import com.dimitrilc.freemediaplayer.data.repos.mediastore.MediaStoreRepository
import com.dimitrilc.freemediaplayer.data.repos.mediastore.MediaStoreRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MediaStoreRepositoryModule {
    @Binds
    abstract fun bindMediaStoreRepository(mediaManager: MediaStoreRepositoryImpl): MediaStoreRepository
}