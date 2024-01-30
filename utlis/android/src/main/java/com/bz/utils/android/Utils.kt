import android.os.Looper

fun throwOnMainThread(methodName: String) {
    check(Looper.myLooper() != Looper.getMainLooper()) {
        "method: $methodName may not be called from main thread."
    }
}
