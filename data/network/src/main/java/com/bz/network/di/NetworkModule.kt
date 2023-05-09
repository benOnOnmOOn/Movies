package com.bz.network.di

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.getSystemService
import com.bz.network.utils.InternetConnection
import com.bz.network.utils.InternetConnectionImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
internal class NetworkModule {

    @Provides
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager? =
        context.getSystemService<ConnectivityManager>()

    @Provides
    fun provideInternetConnection(connectivityManager: ConnectivityManager?): InternetConnection =
        InternetConnectionImpl(connectivityManager)


}
