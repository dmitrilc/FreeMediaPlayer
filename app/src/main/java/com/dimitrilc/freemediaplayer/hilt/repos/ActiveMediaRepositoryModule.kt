package com.dimitrilc.freemediaplayer.hilt.repos

import com.dimitrilc.freemediaplayer.data.repos.activemedia.ActiveMediaRepository
import com.dimitrilc.freemediaplayer.data.repos.activemedia.ActiveMediaRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ActiveMediaRepositoryModule {
    @Binds
    abstract fun bindActiveMediaRepository(activeMediaRepository: ActiveMediaRepositoryImpl): ActiveMediaRepository
}
