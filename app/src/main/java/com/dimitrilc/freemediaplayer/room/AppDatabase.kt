package com.dimitrilc.freemediaplayer.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dimitrilc.freemediaplayer.entities.ActiveMediaItem
import com.dimitrilc.freemediaplayer.entities.GlobalPlaylistItem
import com.dimitrilc.freemediaplayer.entities.MediaItem
import com.dimitrilc.freemediaplayer.entities.ui.FolderItemsUi
import com.dimitrilc.freemediaplayer.entities.ui.ParentPath
import com.dimitrilc.freemediaplayer.entities.ui.RelativePath

@Database(entities = [
        MediaItem::class,
        ParentPath::class,
        RelativePath::class,
        GlobalPlaylistItem::class,
        ActiveMediaItem::class,
        FolderItemsUi::class],
    version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mediaItemDao(): MediaItemDao
    abstract fun parentPathWithRelativePathDao(): ParentPathWithRelativePathsDao
    abstract fun relativePathDao(): RelativePathDao
    abstract fun parentPathDao(): ParentPathDao
    abstract fun folderItemsUiDao(): FolderItemsUiDao
    abstract fun globalPlaylistDao(): GlobalPlaylistDao
    abstract fun activeMediaItemDao(): ActiveMediaItemDao
}