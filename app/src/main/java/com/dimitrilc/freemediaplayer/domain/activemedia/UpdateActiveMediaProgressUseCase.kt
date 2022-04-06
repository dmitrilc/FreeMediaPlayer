package com.dimitrilc.freemediaplayer.domain.activemedia

import com.dimitrilc.freemediaplayer.data.repos.activemedia.ActiveMediaRepository
import com.dimitrilc.freemediaplayer.data.room.dao.ActiveMediaProgress
import javax.inject.Inject

class UpdateActiveMediaProgressUseCase @Inject constructor(private val activeMediaRepository: ActiveMediaRepository) {
    operator fun invoke(activeMediaProgress: ActiveMediaProgress) = activeMediaRepository.updateProgress(activeMediaProgress)
}