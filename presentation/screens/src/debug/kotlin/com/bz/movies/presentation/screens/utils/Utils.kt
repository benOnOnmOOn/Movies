package com.bz.movies.presentation.screens.utils

import androidx.lifecycle.ViewModel
import leakcanary.AppWatcher
import timber.log.Timber

const val VIEW_MODEL_APP_WATCHER_KEY = "VIEW_MODEL_APP_WATCHER_KEY"

inline fun <reified T : ViewModel> T.createCustomAppWatcher() {
    addCloseable(VIEW_MODEL_APP_WATCHER_KEY) {
        val name = T::class.java.simpleName
        Timber.i("$name cleared")
        AppWatcher.objectWatcher.watch(
            watchedObject = this,
            description = "$name received ViewModel#onCleared() callback"
        )
    }
}
