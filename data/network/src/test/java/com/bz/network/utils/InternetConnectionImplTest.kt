package com.bz.network.utils

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class InternetConnectionImplTest {

    private val connectivityManager: ConnectivityManager = mockk(relaxed = true)

    @Test
    fun `when connection manager is null then internet connection should return false`() {
        val internetConnectionImpl = InternetConnectionImpl(null)
        assertFalse(internetConnectionImpl.isConnected)
    }

    @Test
    fun `when getNetworkCapabilities() return is null then internet connection should return false`() {
        every { connectivityManager.getNetworkCapabilities(any()) } returns null
        val internetConnectionImpl = InternetConnectionImpl(connectivityManager)
        assertFalse(internetConnectionImpl.isConnected)
    }

    @Test
    fun `when current network has internet connection when we should return true`() {
        val networkCapabilities: NetworkCapabilities = mockk()
        every {
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } returns true

        every { connectivityManager.getNetworkCapabilities(any()) } returns networkCapabilities
        val internetConnectionImpl = InternetConnectionImpl(connectivityManager)
        assertTrue(internetConnectionImpl.isConnected)
    }

    @Test
    fun `when current network hasn't internet connection when we should return false`() {
        val networkCapabilities: NetworkCapabilities = mockk()
        every {
            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } returns false

        every { connectivityManager.getNetworkCapabilities(any()) } returns networkCapabilities
        val internetConnectionImpl = InternetConnectionImpl(connectivityManager)
        assertFalse(internetConnectionImpl.isConnected)
    }
}
