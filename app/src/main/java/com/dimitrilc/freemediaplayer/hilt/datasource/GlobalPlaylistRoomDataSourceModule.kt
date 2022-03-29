package com.dimitrilc.freemediaplayer.hilt.datasource

import com.dimitrilc.freemediaplayer.data.datasources.GlobalPlaylistRoomDataSource
import com.dimitrilc.freemediaplayer.data.datasources.GlobalPlaylistRoomDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class GlobalPlaylistRoomDataSourceModule {
    @Binds
    abstract fun bindGlobalPlaylistRoomDataSource(
        globalPlaylistRoomDataSource: GlobalPlaylistRoomDataSourceImpl
    ): GlobalPlaylistRoomDataSource
}