package com.bz.movies.database

import android.runSuspendCatching
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal suspend inline fun <R> runCatchingWithContext(
    context: CoroutineContext = Dispatchers.IO,
    crossinline block: suspend () -> R
): Result<R> = withContext(context) {
    runSuspendCatching { block() }
}
