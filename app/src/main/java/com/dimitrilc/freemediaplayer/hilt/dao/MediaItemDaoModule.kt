package com.dimitrilc.freemediaplayer.hilt.dao

import com.dimitrilc.freemediaplayer.data.room.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MediaItemDaoModule {

    @Provides
    fun provideMediaItemDao(db: AppDatabase) = db.mediaItemDao()

}