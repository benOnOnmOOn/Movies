package com.bz.network.utils

import android.os.Looper
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic

/**
 * `throwOnMainThread()` compares Looper.myLooper() with Looper.getMainLooper(). With
 * `isReturnDefaultValues` both return null on the JVM, so stub them as two distinct loopers
 * to simulate running off the main thread.
 */
internal fun stubBackgroundLooper() {
    mockkStatic(Looper::class)
    every { Looper.myLooper() } returns mockk()
    every { Looper.getMainLooper() } returns mockk()
}

internal fun clearLooperStub() = unmockkStatic(Looper::class)
