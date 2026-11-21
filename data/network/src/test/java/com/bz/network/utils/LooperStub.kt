package com.bz.network.utils

import android.os.Looper
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic

internal fun stubBackgroundLooper() {
    mockkStatic(Looper::class)
    every { Looper.myLooper() } returns mockk()
    every { Looper.getMainLooper() } returns mockk()
}

internal fun clearLooperStub() = unmockkStatic(Looper::class)
