package com.bz.movies.core

import android.app.Application
import com.bz.movies.core.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MoviesApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MoviesApp)
            logger(AndroidLogger(level = Level.ERROR))
            modules(appModules)
        }
    }
}