package com.dimitrilc.freemediaplayer.domain.activemedia

import com.dimitrilc.freemediaplayer.data.repos.activemedia.ActiveMediaRepository
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaIsPlaying
import javax.inject.Inject

class UpdateActiveMediaIsPlayingUseCase @Inject constructor(
    private val activeMediaRepository: ActiveMediaRepository) {
    operator fun invoke(isPlaying: ActiveMediaIsPlaying) =
        activeMediaRepository.updateIsPlaying(isPlaying)
}