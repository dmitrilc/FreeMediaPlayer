package com.dimitrilc.freemediaplayer.domain.activemedia

import com.dimitrilc.freemediaplayer.data.repos.activemedia.ActiveMediaRepository
import javax.inject.Inject

class GetActiveMediaOnceUseCase @Inject constructor(
    private val activeMediaRepository: ActiveMediaRepository) {
    suspend operator fun invoke() = activeMediaRepository.getOnce()
}