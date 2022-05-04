package com.dimitrilc.freemediaplayer.hilt.datasource

import com.dimitrilc.freemediaplayer.data.source.room.mediaitem.MediaItemLocalDataSource
import com.dimitrilc.freemediaplayer.data.source.room.mediaitem.MediaItemLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MediaItemRoomDataSourceModule {
    @Binds
    abstract fun bindMediaItemRoomDataSource(
        mediaItemRoomDataSource: MediaItemLocalDataSourceImpl
    ): MediaItemLocalDataSource
}