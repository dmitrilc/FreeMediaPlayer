package com.dimitrilc.freemediaplayer.hilt.datasource

import com.dimitrilc.freemediaplayer.data.datasources.ActiveMediaRoomDataSource
import com.dimitrilc.freemediaplayer.data.datasources.ActiveMediaRoomDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ActiveMediaDataSourceModule {
    @Binds
    abstract fun bindActiveMediaDataSource(activeMediaDataSource: ActiveMediaRoomDataSourceImpl): ActiveMediaRoomDataSource
}