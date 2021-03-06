package com.dimitrilc.freemediaplayer.domain.activemedia

import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import com.dimitrilc.freemediaplayer.data.repos.activemedia.ActiveMediaRepository
import javax.inject.Inject

class UpdateActiveMediaByObjectUseCase @Inject constructor(private val activeMediaRepository: ActiveMediaRepository) {
    operator fun invoke(activeMedia: ActiveMedia) = activeMediaRepository.update(activeMedia)
}