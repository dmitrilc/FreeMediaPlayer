package com.dimitrilc.freemediaplayer.domain.activemedia

import com.dimitrilc.freemediaplayer.data.repos.activemedia.ActiveMediaRepository
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaPlaylistPosition
import javax.inject.Inject

class UpdateActiveMediaPlaylistPositionAndMediaIdUseCase @Inject constructor(
    private val activeMediaRepository: ActiveMediaRepository
) {
    operator fun invoke(playlistPos: Long){
        activeMediaRepository.updatePlaylistPosition(
            ActiveMediaPlaylistPosition(globalPlaylistPosition = playlistPos)
        )
    }
}