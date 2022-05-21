package com.dimitrilc.freemediaplayer.hilt.repos

import com.dimitrilc.freemediaplayer.data.repos.globalplaylist.GlobalPlaylistRepository
import com.dimitrilc.freemediaplayer.data.repos.globalplaylist.GlobalPlaylistRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class GlobalPlaylistRepositoryModule {
    @Binds
    abstract fun bindGlobalPlaylistRepository(globalPlaylistRepository: GlobalPlaylistRepositoryImpl): GlobalPlaylistRepository
}
