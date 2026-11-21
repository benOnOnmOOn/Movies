package com.bz.network.di

import com.bz.tools.DelegatingSocketFactory
import com.bz.tools.throwOnMainThread
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@Module
@InstallIn(SingletonComponent::class)
internal object ApiModule {
    @Singleton
    @Provides
    internal fun provideOkHttpClient(): OkHttpClient {
        throwOnMainThread()
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) }
            )
            .socketFactory(DelegatingSocketFactory())
            .build()
    }
}
