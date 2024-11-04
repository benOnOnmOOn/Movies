package com.bz.movies.core

import timber.log.Timber

internal class CrashlyticsLogTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        println("Something went wrong")
    }
}
