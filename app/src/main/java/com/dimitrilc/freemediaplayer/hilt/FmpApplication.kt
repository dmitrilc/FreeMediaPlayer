package com.dimitrilc.freemediaplayer.hilt

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

//This class does not perform anything different from official docs, no need to test
@HiltAndroidApp
class FmpApplication : Application(), Configuration.Provider {

    //https://developer.android.com/training/dependency-injection/hilt-jetpack#workmanager
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

}
