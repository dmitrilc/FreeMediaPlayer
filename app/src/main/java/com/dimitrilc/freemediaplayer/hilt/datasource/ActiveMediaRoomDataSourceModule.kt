package com.dimitrilc.freemediaplayer.hilt.datasource

import com.dimitrilc.freemediaplayer.data.datasources.ActiveMediaRoomDataSource
import com.dimitrilc.freemediaplayer.data.datasources.ActiveMediaRoomDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ActiveMediaRoomDataSourceModule {
    @Binds
    abstract fun bindActiveMediaRoomDataSource(
        activeMediaRoomDataSource: ActiveMediaRoomDataSourceImpl
    ): ActiveMediaRoomDataSource
}