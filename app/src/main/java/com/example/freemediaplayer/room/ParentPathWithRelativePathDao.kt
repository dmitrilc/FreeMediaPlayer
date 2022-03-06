package com.example.freemediaplayer.room

import androidx.room.*
import com.example.freemediaplayer.entities.ui.ParentPath
import com.example.freemediaplayer.entities.ui.ParentPathWithRelativePaths
import com.example.freemediaplayer.entities.ui.RelativePath
import kotlinx.coroutines.flow.Flow

@Dao
interface ParentPathWithRelativePathsDao {

    //TODO Distinguish between audio and video
    @Transaction
    @Query("SELECT * FROM parentPath WHERE parentPath.isAudio=1")
    fun getAudioParentPathWithRelativePaths(): Flow<List<ParentPathWithRelativePaths>>

    @Transaction
    @Query("SELECT * FROM parentPath WHERE parentPath.isAudio=0")
    fun getVideoParentPathWithRelativePaths(): Flow<List<ParentPathWithRelativePaths>>

    @Transaction
    suspend fun insertAudioParentPathsWithRelativePaths(map: Map<String, List<String>>){
        map.forEach {
            val tmpId = insertParentPath(ParentPath(isAudio = true, parentPath = it.key))

            val id = if (tmpId == -1L){
                getParentPathId(isAudio = true, it.key)
            } else {
                tmpId
            }

            val relativePaths = it.value.map { path ->
                RelativePath(
                    parentPathId = id,
                    relativePath = path
                )
            }

            insertRelativePaths(relativePaths)
        }
    }

    @Transaction
    suspend fun insertVideoParentPathsWithRelativePaths(map: Map<String, List<String>>){
        map.forEach {
            val tmpId = insertParentPath(ParentPath(isAudio = false, parentPath = it.key))

            val id = if (tmpId == -1L){
                getParentPathId(isAudio = false, it.key)
            } else {
                tmpId
            }

            val relativePaths = it.value.map { path ->
                RelativePath(
                    parentPathId = id,
                    relativePath = path
                )
            }

            insertRelativePaths(relativePaths)
        }
    }

    @Query("SELECT id FROM parentPath WHERE isAudio=:isAudio AND parentPath=:parentPath")
    suspend fun getParentPathId(isAudio: Boolean, parentPath: String): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertParentPath(parentPath: ParentPath): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRelativePaths(relativePaths: Collection<RelativePath>)

    @Update
    suspend fun updateParentPath(parentPath: ParentPath)
}