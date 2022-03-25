package com.dimitrilc.freemediaplayer.hilt

import com.dimitrilc.freemediaplayer.room.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object RelativePathDaoModule {

    @Provides
    fun provideRelativePathsDao(db: AppDatabase) = db.relativePathDao()
}