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


            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
}