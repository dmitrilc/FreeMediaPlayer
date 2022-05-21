package com.dimitrilc.freemediaplayer.domain.globalplaylist

import com.dimitrilc.freemediaplayer.data.repos.globalplaylist.GlobalPlaylistRepository
import javax.inject.Inject

class GetGlobalPlaylistOnceUseCase @Inject constructor(
    private val globalPlaylistRepository: GlobalPlaylistRepository
) {

    suspend operator fun invoke() = globalPlaylistRepository.getAllOnce()
}