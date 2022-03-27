package com.dimitrilc.freemediaplayer.hilt.datasource

import com.dimitrilc.freemediaplayer.data.datasources.GlobalPlaylistRoomDataSource
import com.dimitrilc.freemediaplayer.data.datasources.GlobalPlaylistRoomDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class GlobalPlaylistDataSourceModule {
    @Binds
    abstract fun bindGlobalPlaylistDataSource(globalPlaylistDataSource: GlobalPlaylistRoomDataSourceImpl): GlobalPlaylistRoomDataSource
}