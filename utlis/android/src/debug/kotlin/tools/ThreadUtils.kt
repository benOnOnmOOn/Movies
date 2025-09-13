package com.bz.tools

import android.os.Looper

@Suppress("NOTHING_TO_INLINE")
public inline fun throwOnMainThread() {
    check(Looper.myLooper() != Looper.getMainLooper()) {
        val methodName = object {}.javaClass.enclosingMethod?.name ?: "Unknown"
        "method: $methodName may not be called from main thread."
    }
}


