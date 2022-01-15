package com.example.freemediaplayer.hilt

import com.example.freemediaplayer.room.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AudioDaoModule {

    @Provides
    fun provideAudioDao(db: AppDatabase) = db.audioDao()

}