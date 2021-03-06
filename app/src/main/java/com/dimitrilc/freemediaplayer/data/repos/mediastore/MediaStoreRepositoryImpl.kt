package com.dimitrilc.freemediaplayer.data.repos.mediastore

import com.dimitrilc.freemediaplayer.data.source.mediastore.MediaStoreDataSource
import javax.inject.Inject

class MediaStoreRepositoryImpl
@Inject constructor(private val mediaStoreDataSource: MediaStoreDataSource)
    : MediaStoreRepository {
    override fun queryAudios() = mediaStoreDataSource.queryAudios()
    override fun queryVideos() = mediaStoreDataSource.queryVideos()

    override fun getAlbumArtUriBeforeQ(albumId: Int) = mediaStoreDataSource.getAlbumArtUriBeforeQ(albumId)
    override fun getThumbnail(artUri: String?, videoId: Long?) = mediaStoreDataSource.getThumbnail(artUri, videoId)
}