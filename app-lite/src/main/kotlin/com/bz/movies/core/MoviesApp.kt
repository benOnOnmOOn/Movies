package com.bz.movies.core

import android.app.Application
import android.os.StrictMode
import co.touchlab.kermit.Logger
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.Executors

@HiltAndroidApp
class MoviesApp : Application() {
    override fun onCreate() {
        super.onCreate()
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyListener(Executors.newSingleThreadScheduledExecutor()) {
                    Logger.e("StrictMode ThreadPolicy violation", it)
                }
                .build()
        )

        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyListener(Executors.newSingleThreadScheduledExecutor()) {
                    Logger.e("StrictMode Vm Policy violation", it)
                }
                .build()
        )
    }
}