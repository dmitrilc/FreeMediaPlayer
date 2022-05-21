package com.dimitrilc.freemediaplayer.domain.activemedia

import com.dimitrilc.freemediaplayer.data.entities.ActiveMedia
import com.dimitrilc.freemediaplayer.data.repos.activemedia.ActiveMediaRepository
import javax.inject.Inject

class InsertActiveMediaUseCase @Inject constructor(private val activeMediaRepository: ActiveMediaRepository) {
    operator fun invoke(activeMedia: ActiveMedia) = activeMediaRepository.insert(activeMedia)
}