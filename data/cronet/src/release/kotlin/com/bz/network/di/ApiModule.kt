package com.bz.network.di

import android.DelegatingSocketFactory
import android.content.Context
import android.throwOnMainThread
import com.google.android.gms.net.CronetProviderInstaller
import com.google.android.gms.tasks.Tasks
import com.google.net.cronet.okhttptransport.CronetCallFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import org.chromium.net.CronetEngine

internal const val NETWORK_CONNECTION_TIMEOUT = 30L

@Module
@InstallIn(SingletonComponent::class)
internal class ApiModule {
    @Provides
    internal fun provideCronetEngine(@ApplicationContext context: Context): CronetEngine {
        throwOnMainThread("provideCronetEngine")
        Tasks.await(CronetProviderInstaller.installProvider(context))

        return CronetEngine.Builder(context)
            .enableBrotli(true)
            .enableQuic(true)
            .build()
    }

    @Provides
    internal fun provideCronetCallFactory(engine: CronetEngine): CronetCallFactory {
        throwOnMainThread("provideCronetCallFactory")
        return CronetCallFactory.newBuilder(engine).build()
    }

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        throwOnMainThread("provideOkHttpClient")
        return OkHttpClient.Builder()
            .connectTimeout(NETWORK_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(NETWORK_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(NETWORK_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            .socketFactory(DelegatingSocketFactory())
            .build()
    }
}
