package com.bz.movies.core

import android.app.Application
import android.enableStrictMode
import android.os.Build
import android.os.StrictMode
import co.touchlab.kermit.Logger
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.Executors

@HiltAndroidApp
class MoviesApp : Application() {
    override fun onCreate() {
        super.onCreate()
        enableStrictMode()
    }
}
