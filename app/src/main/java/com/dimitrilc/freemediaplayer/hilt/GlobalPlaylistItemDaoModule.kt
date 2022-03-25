package com.dimitrilc.freemediaplayer.hilt

import com.dimitrilc.freemediaplayer.room.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object GlobalPlaylistItemDaoModule {

    @Provides
    fun provideGlobalPlaylistDao(db: AppDatabase) = db.folderItemsUiDao()
}