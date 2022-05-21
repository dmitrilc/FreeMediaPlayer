package com.dimitrilc.freemediaplayer.hilt.datasource

import com.dimitrilc.freemediaplayer.data.source.mediastore.MediaStoreDataSource
import com.dimitrilc.freemediaplayer.data.source.mediastore.MediaStoreDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MediaStoreDataSourceModule {
    @Binds
    abstract fun bindMediaStoreDataSource(
        mediaStoreDataSource: MediaStoreDataSourceImpl
    ): MediaStoreDataSource
}