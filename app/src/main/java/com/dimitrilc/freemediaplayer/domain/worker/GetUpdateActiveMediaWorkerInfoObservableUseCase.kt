package com.dimitrilc.freemediaplayer.domain.worker

import android.app.Application
import androidx.work.WorkManager
import java.util.*
import javax.inject.Inject

class GetUpdateActiveMediaWorkerInfoObservableUseCase @Inject constructor(
    private val app: Application) {

    operator fun invoke(uuid: String) = WorkManager
        .getInstance(app.applicationContext)
        .getWorkInfoByIdLiveData(
            UUID.fromString(uuid)
        )
}