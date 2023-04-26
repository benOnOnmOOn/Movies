package com.bz.network.utils

import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class InternetConnectionImpl(
    private val connectivityManager: ConnectivityManager,
) : InternetConnection {

    override val isConnected: Boolean
        get() {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    ?: return false


            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
}
