package android

import android.os.Build
import android.os.Looper
import android.os.StrictMode
import co.touchlab.kermit.Logger
import java.util.concurrent.Executors
import kotlin.coroutines.cancellation.CancellationException

fun throwOnMainThread() {
    check(Looper.myLooper() != Looper.getMainLooper()) {
        val methodName = object {}.javaClass.enclosingMethod?.name ?: "Unknown"
        "method: $methodName may not be called from main thread."
    }
}

@Suppress("TooGenericExceptionCaught")
inline fun <R> runSuspendCatching(block: () -> R): Result<R> = try {
    Result.success(block())
} catch (c: CancellationException) {
    throw c
} catch (e: Throwable) {
    Result.failure(e)
}

fun enableStrictMode() {
    StrictMode.setThreadPolicy(
        StrictMode.ThreadPolicy.Builder()
            .detectAll()
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    penaltyListener(Executors.newSingleThreadScheduledExecutor()) {
                        Logger.e("StrictMode ThreadPolicy violation", it)
                    }
                } else {
                    penaltyLog()
                }
            }
            .build()
    )

    StrictMode.setVmPolicy(
        StrictMode.VmPolicy.Builder()
            .detectAll()
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    penaltyListener(Executors.newSingleThreadScheduledExecutor()) {
                        Logger.e("StrictMode Vm Policy violation", it)
                    }
                } else {
                    penaltyLog()
                }
            }
            .build()
    )
}
