package com.dimitrilc.freemediaplayer.worker

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import androidx.hilt.work.HiltWorker
import androidx.lifecycle.viewModelScope
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dimitrilc.freemediaplayer.entities.MediaItem
import com.dimitrilc.freemediaplayer.isSameOrAfterQ
import com.dimitrilc.freemediaplayer.room.AppDatabase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltWorker
class MediaScanWorker
@AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val appDb: AppDatabase) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val allAudios = queryAudios()
        val allVideos = queryVideos()

        val audioPaths = toParentPathsWithRelativePaths(allAudios)
        val videoPaths = toParentPathsWithRelativePaths(allVideos)

        appDb.parentPathWithRelativePathDao().insertAudioParentPathsWithRelativePaths(audioPaths)
        appDb.parentPathWithRelativePathDao().insertVideoParentPathsWithRelativePaths(videoPaths)

        insertMediaItems(allAudios.plus(allVideos))

        return Result.success()
    }

    private fun queryAudios(): List<MediaItem> {
        val allAudios = mutableListOf<MediaItem>()

        val collection =
            if (isSameOrAfterQ()) {
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

        val projection = mutableListOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val selection = null
        val selectionArgs = null
        val sortOrder = null

        appContext.contentResolver.query(
            collection,
            projection.toTypedArray(),
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val dataColIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val titleColIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val albumColIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val albumIdColIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColIndex)
                val data = cursor.getString(dataColIndex)

                val uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val displayName = data.substringAfterLast('/')
                val location = data.substringBeforeLast('/')

                val albumId = cursor.getInt(albumIdColIndex)

                val albumArtUri = if (isSameOrAfterQ()){
                    uri.toString()
                } else {
                    getAlbumArtUriBeforeQ(albumId)
                }

                val audio = MediaItem(
                    id = id,
                    uri = uri,
                    data = data,
                    displayName = displayName,
                    title = cursor.getString(titleColIndex),
                    location = location,
                    isAudio = true,
                    album = cursor.getString(albumColIndex),
                    albumId = albumId,
                    albumArtUri = albumArtUri
                )

                allAudios.add(audio)
            }

        }

        return allAudios
    }

    private fun queryVideos(): List<MediaItem> {
        val allVideos = mutableListOf<MediaItem>()

        val collection =
            if (isSameOrAfterQ()) {
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }

        val projection = mutableListOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.ALBUM,
            MediaStore.Video.Media.DATA
        )

        val selection = null
        val selectionArgs = null
        val sortOrder = null

        appContext.contentResolver.query(
            collection,
            projection.toTypedArray(),
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColIndex = cursor.getColumnIndex(MediaStore.Video.Media._ID)
            val dataColIndex = cursor.getColumnIndex(MediaStore.Video.Media.DATA)
            val titleColIndex = cursor.getColumnIndex(MediaStore.Video.Media.TITLE)
            val albumColIndex = cursor.getColumnIndex(MediaStore.Video.Media.ALBUM)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColIndex)
                val data = cursor.getString(dataColIndex)

                val uri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val displayName = data.substringAfterLast('/')
                val location = data.substringBeforeLast('/')

                val video = MediaItem(
                    id = id,
                    uri = uri,
                    data = data,
                    displayName = displayName,
                    title = cursor.getString(titleColIndex),
                    location = location,
                    isAudio = false,
                    album = cursor.getString(albumColIndex),
                    albumId = -1,
                    albumArtUri = uri.toString()
                )

                allVideos.add(video)
            }
        }

        return allVideos
    }

    private fun getAlbumArtUriBeforeQ(albumId: Int): String? {
        val collection = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM_ART
        )
        val selection = "${MediaStore.Audio.Albums._ID} = ?"
        val selectionArgs = arrayOf("$albumId")
        val sortOrder = null

        var albumArtUri: String? = null

        appContext.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val albumArtColIndex = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)

            while (cursor.moveToNext()) {
                albumArtUri = cursor.getString(albumArtColIndex)
            }
        }

        return albumArtUri
    }

    private fun toParentPathsWithRelativePaths(list: List<MediaItem>): Map<String, List<String>> {
        return list.asSequence()
            .distinctBy { it.location }
            .map { it.location }
            .groupBy({ it.substringBeforeLast('/') }) {
                it.substringAfterLast('/')
            }
    }

    private suspend fun insertMediaItems(items: Collection<MediaItem>) {
        appDb.mediaItemDao().insertAll(items)
    }
}