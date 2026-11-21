package com.bz.network.di

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.getSystemService
import com.bz.network.utils.InternetConnection
import com.bz.network.utils.InternetConnectionImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    @Singleton
    @Provides
    internal fun provideConnectivityManager(
        @ApplicationContext context: Context
    ): ConnectivityManager? = context.getSystemService<ConnectivityManager>()
}

@Module
@InstallIn(SingletonComponent::class)
internal interface NetworkBindingModule {
    @Binds
    fun bindInternetConnection(impl: InternetConnectionImpl): InternetConnection
}
