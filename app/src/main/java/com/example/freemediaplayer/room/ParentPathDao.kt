package com.example.freemediaplayer.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.freemediaplayer.entities.ui.ParentPath

@Dao
interface ParentPathDao {

//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun insertParentPath(parentPath: ParentPath)

/*    @Insert(onConflict = REPLACE)
    suspend fun insertParentPaths(parentPaths: Collection<ParentPath>)

    @Insert(onConflict = REPLACE)
    suspend fun insertParentPath(parentPath: ParentPath)

    @Query("SELECT * from parentPath " +
            "WHERE isAudio='true'" +
            " AND parentPath='path'")
    suspend fun getAudioParentPath(path: String): ParentPath*/
}