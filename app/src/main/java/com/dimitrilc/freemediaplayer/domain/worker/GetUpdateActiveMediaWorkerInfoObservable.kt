package com.dimitrilc.freemediaplayer.domain.worker

import android.app.Application
import androidx.work.WorkManager
import com.dimitrilc.freemediaplayer.data.repos.MediaManager
import java.util.*
import javax.inject.Inject

class GetUpdateActiveMediaWorkerInfoObservable @Inject constructor(private val app: Application) {
    operator fun invoke(uuid: String) = WorkManager
        .getInstance(app.applicationContext)
        .getWorkInfoByIdLiveData(
            UUID.fromString(uuid)
        )
}