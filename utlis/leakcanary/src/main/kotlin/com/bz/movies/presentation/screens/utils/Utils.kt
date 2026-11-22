package com.bz.movies.presentation.screens.utils

import androidx.lifecycle.ViewModel
import co.touchlab.kermit.Logger
import leakcanary.AppWatcher

public const val VIEW_MODEL_APP_WATCHER_KEY: String = "VIEW_MODEL_APP_WATCHER_KEY"

public fun ViewModel.createCustomAppWatcher() {
    addCloseable(VIEW_MODEL_APP_WATCHER_KEY) {
        val name = javaClass.simpleName
        Logger.i("$name cleared")
        @Suppress("DeprecatedCall")
        AppWatcher.objectWatcher.watch(
            watchedObject = this,
            description = "$name received ViewModel#onCleared() callback"
        )
    }
}
