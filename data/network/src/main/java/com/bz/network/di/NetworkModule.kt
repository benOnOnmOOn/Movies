package com.bz.network.di

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.getSystemService
import com.bz.network.utils.InternetConnection
import com.bz.network.utils.InternetConnectionImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import throwOnMainThread

@Module
@InstallIn(ViewModelComponent::class)
internal class NetworkModule {
    @Provides
    internal fun provideConnectivityManager(
        @ApplicationContext context: Context
    ): ConnectivityManager? {
        throwOnMainThread("provideInternetConnection")
        return context.getSystemService<ConnectivityManager>()
    }

    @Provides
    internal fun provideInternetConnection(
        connectivityManager: ConnectivityManager?
    ): InternetConnection {
        throwOnMainThread("provideInternetConnection")
        return InternetConnectionImpl(connectivityManager)
    }
}
