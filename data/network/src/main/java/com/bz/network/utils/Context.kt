package com.bz.network.utils

import android.content.Context

inline fun <reified T> Context.getSystemService(): T = getSystemService(T::class.java)
