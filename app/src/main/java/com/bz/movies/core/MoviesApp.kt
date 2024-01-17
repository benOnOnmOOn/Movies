package com.bz.movies.core

import android.app.Application
import android.os.StrictMode
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.Executors
import timber.log.Timber

@HiltAndroidApp
class MoviesApp : Application() {
    override fun onCreate() {
        super.onCreate()
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyListener(Executors.newSingleThreadScheduledExecutor(), Timber::e)
                .build()
        )
    }
}
