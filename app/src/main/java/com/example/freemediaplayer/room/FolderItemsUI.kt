package com.example.freemediaplayer.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.freemediaplayer.entities.ui.FolderItemsUi

@Dao
interface FolderItemsUiDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentFolderItemsUi(folderItemsUi: FolderItemsUi)
}
