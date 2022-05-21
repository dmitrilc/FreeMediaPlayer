package com.dimitrilc.freemediaplayer.hilt

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FmpApplicationModule {

    @Provides
    @Singleton
    fun provideFmpApplication(application: Application) : FmpApplication = application as FmpApplication
}