package com.dimitrilc.freemediaplayer.hilt.datasource

import com.dimitrilc.freemediaplayer.data.datasources.MediaItemRoomDataSource
import com.dimitrilc.freemediaplayer.data.datasources.MediaItemRoomDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MediaItemRoomDataSourceModule {
    @Binds
    abstract fun bindMediaItemRoomDataSource(
        mediaItemRoomDataSource: MediaItemRoomDataSourceImpl
    ): MediaItemRoomDataSource
}