package com.dimitrilc.freemediaplayer.hilt.repos

import com.dimitrilc.freemediaplayer.data.repos.mediaitem.MediaItemRepository
import com.dimitrilc.freemediaplayer.data.repos.mediaitem.MediaItemRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MediaItemRepositoryModule {
    @Binds
    abstract fun bindMediaItemRepository(mediaItemRepository: MediaItemRepositoryImpl): MediaItemRepository
}
