package com.dimitrilc.freemediaplayer.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dimitrilc.freemediaplayer.entities.ui.FolderItemsUi

@Dao
interface FolderItemsUiDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentFolderItemsUi(folderItemsUi: FolderItemsUi)

    @Query("SELECT * FROM folderItemsUi LIMIT 1")
    suspend fun getCurrentFolderItemsUi(): FolderItemsUi
}
