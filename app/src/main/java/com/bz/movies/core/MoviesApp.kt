package com.bz.movies.core

import android.app.Application
import android.enableStrictMode
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MoviesApp : Application() {
    override fun onCreate() {
        super.onCreate()
        enableStrictMode()
    }
}
