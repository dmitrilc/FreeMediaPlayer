package com.example.freemediaplayer.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.freemediaplayer.entities.ui.ParentPath
import com.example.freemediaplayer.entities.ui.RelativePath

@Dao
interface RelativePathDao {

/*    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelativePaths(relativePaths: Collection<RelativePath>)*/

}