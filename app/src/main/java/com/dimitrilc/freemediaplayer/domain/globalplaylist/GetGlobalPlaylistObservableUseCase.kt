package com.dimitrilc.freemediaplayer.domain.globalplaylist

import com.dimitrilc.freemediaplayer.data.repos.globalplaylist.GlobalPlaylistRepository
import javax.inject.Inject

class GetGlobalPlaylistObservableUseCase @Inject constructor(
    private val globalPlaylistRepository: GlobalPlaylistRepository
) {

    operator fun invoke() = globalPlaylistRepository.getAllObservable()
}