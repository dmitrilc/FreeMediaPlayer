package com.dimitrilc.freemediaplayer.domain.activemedia

import com.dimitrilc.freemediaplayer.data.repos.activemedia.ActiveMediaRepository
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaPlaylistPosition
import javax.inject.Inject

class UpdateActiveMediaByGlobalPlaylistPositionUseCase
@Inject constructor(private val activeMediaRepository: ActiveMediaRepository) {
    operator fun invoke(pos: Int) = activeMediaRepository.updatePlaylistPosition(
        ActiveMediaPlaylistPosition(globalPlaylistPosition = pos.toLong())
    )
}