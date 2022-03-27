package com.dimitrilc.freemediaplayer.hilt.repos

import com.dimitrilc.freemediaplayer.data.repos.ActiveMediaRepository
import com.dimitrilc.freemediaplayer.data.repos.ActiveMediaRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ActiveMediaRepositoryModule {
    @Binds
    abstract fun bindActiveMediaRepository(activeMediaRepository: ActiveMediaRepositoryImpl): ActiveMediaRepository
}
