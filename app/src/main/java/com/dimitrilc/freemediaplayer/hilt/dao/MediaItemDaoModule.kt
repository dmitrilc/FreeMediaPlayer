package com.dimitrilc.freemediaplayer.hilt.dao

import com.dimitrilc.freemediaplayer.data.room.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object MediaItemDaoModule {

    @Provides
    fun provideMediaItemDao(db: AppDatabase) = db.mediaItemDao()

}