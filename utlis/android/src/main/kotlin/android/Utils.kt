import android.os.Looper
import kotlin.coroutines.cancellation.CancellationException

fun throwOnMainThread(methodName: String) {
    check(Looper.myLooper() != Looper.getMainLooper()) {
        "method: $methodName may not be called from main thread."
    }
}

@Suppress("TooGenericExceptionCaught")
inline fun <R> runSuspendCatching(block: () -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (c: CancellationException) {
        throw c
    } catch (e: Throwable) {
        Result.failure(e)
    }
}
