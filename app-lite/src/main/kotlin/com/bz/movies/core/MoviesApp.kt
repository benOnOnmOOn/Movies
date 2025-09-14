package com.bz.movies.core

import android.app.Application
import com.bz.tools.enableStrictMode
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
internal class MoviesApp : Application() {
    override fun onCreate() {
        super.onCreate()
        enableStrictMode()
    }
}
