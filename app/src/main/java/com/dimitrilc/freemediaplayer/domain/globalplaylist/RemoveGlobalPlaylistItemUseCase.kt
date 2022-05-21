package com.dimitrilc.freemediaplayer.domain.globalplaylist

import com.dimitrilc.freemediaplayer.data.entities.GlobalPlaylistItem
import com.dimitrilc.freemediaplayer.data.repos.globalplaylist.GlobalPlaylistRepository
import javax.inject.Inject

class RemoveGlobalPlaylistItemUseCase @Inject constructor(
    private val globalPlaylistRepository: GlobalPlaylistRepository
) {

    operator fun invoke(item: GlobalPlaylistItem) = globalPlaylistRepository.removeItem(item)

    operator fun invoke(position: Long) = globalPlaylistRepository.removeItemAtPosition(position)
}