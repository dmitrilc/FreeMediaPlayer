package com.dimitrilc.freemediaplayer.hilt.datasource

import com.dimitrilc.freemediaplayer.data.source.room.activemedia.ActiveMediaLocalDataSource
import com.dimitrilc.freemediaplayer.data.source.room.activemedia.ActiveMediaLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ActiveMediaRoomDataSourceModule {
    @Binds
    abstract fun bindActiveMediaRoomDataSource(
        activeMediaRoomDataSource: ActiveMediaLocalDataSourceImpl
    ): ActiveMediaLocalDataSource
}