package com.bz.network.di

import com.bz.tools.DelegatingSocketFactory
import com.bz.tools.throwOnMainThread
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient

private const val NETWORK_CONNECTION_TIMEOUT = 30L

@Module
@InstallIn(ViewModelComponent::class)
internal class ApiModule {

    @Provides
    internal fun provideOkHttpClient(): OkHttpClient {
        throwOnMainThread()
        return OkHttpClient.Builder()
            .connectTimeout(NETWORK_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(NETWORK_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(NETWORK_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .socketFactory(DelegatingSocketFactory())
            .build()
    }
}
