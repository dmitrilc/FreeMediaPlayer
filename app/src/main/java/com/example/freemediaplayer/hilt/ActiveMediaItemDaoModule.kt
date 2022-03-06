package com.example.freemediaplayer.hilt

import com.example.freemediaplayer.room.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object ActiveMediaItemDaoModule {

    @Provides
    fun provideActiveMediaItemDao(db: AppDatabase) = db.activeMediaItemDao()
}