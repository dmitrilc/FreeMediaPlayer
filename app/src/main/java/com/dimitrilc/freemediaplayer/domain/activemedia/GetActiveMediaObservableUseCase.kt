package com.dimitrilc.freemediaplayer.domain.activemedia

import com.dimitrilc.freemediaplayer.data.repos.activemedia.ActiveMediaRepository
import javax.inject.Inject

class GetActiveMediaObservableUseCase @Inject constructor(
    private val activeMediaRepository: ActiveMediaRepository
    ) {
    operator fun invoke() = activeMediaRepository.getObservable()
}