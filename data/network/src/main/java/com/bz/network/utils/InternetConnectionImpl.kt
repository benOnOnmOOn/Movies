package com.bz.network.utils

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.bz.tools.throwOnMainThread
import javax.inject.Inject

internal class InternetConnectionImpl @Inject constructor(
    private val connectivityManager: ConnectivityManager?
) : InternetConnection {
    init {
        throwOnMainThread()
    }

    override val isConnected: Boolean
        get() =
            connectivityManager
                ?.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
}
