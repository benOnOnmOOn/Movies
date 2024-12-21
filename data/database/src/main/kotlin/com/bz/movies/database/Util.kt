package com.bz.movies.database

import android.runSuspendCatching
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

suspend inline fun <R> runCatchingWithContext(
    context: CoroutineContext = Dispatchers.IO,
    crossinline  block: suspend () -> R
): Result<R> {
    return withContext(context) {
        runSuspendCatching { block() }
    }
}