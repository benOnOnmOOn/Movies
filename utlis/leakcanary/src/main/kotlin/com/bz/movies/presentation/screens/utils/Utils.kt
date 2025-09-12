package com.bz.movies.presentation.screens.utils

import androidx.lifecycle.ViewModel
import co.touchlab.kermit.Logger
import leakcanary.AppWatcher

public const val VIEW_MODEL_APP_WATCHER_KEY: String = "VIEW_MODEL_APP_WATCHER_KEY"

public inline fun <reified T : ViewModel> T.createCustomAppWatcher() {
    addCloseable(VIEW_MODEL_APP_WATCHER_KEY) {
        val name = T::class.java.simpleName
        Logger.i("$name cleared")
        @Suppress("DeprecatedCall")
        AppWatcher.objectWatcher.watch(
            watchedObject = this,
            description = "$name received ViewModel#onCleared() callback"
        )
    }
}
