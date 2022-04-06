package com.dimitrilc.freemediaplayer.domain.activemedia

import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import com.dimitrilc.freemediaplayer.data.repos.activemedia.ActiveMediaRepository
import javax.inject.Inject

class InsertDefaultActiveMediaUseCase @Inject constructor(private val activeMediaRepository: ActiveMediaRepository) {
    operator fun invoke(currentItemPos: Long, id: Long){
        activeMediaRepository.insert(
            ActiveMedia(
                globalPlaylistPosition = currentItemPos,
                mediaItemId = id
            )
        )
    }
}