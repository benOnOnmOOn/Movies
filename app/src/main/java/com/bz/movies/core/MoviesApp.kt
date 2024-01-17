package com.bz.movies.core

import android.app.Application
import android.os.StrictMode
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.concurrent.Executors

@HiltAndroidApp
class MoviesApp : Application() {
    override fun onCreate() {
        super.onCreate()
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyDialog()
                .penaltyListener(Executors.newSingleThreadScheduledExecutor(), Timber::e)
                .build(),
        )
    }
}
