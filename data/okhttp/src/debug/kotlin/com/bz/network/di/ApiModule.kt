package com.bz.network.di

import android.DelegatingSocketFactory
import android.throwOnMainThread
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@Module
@InstallIn(ViewModelComponent::class)
internal class ApiModule {
    @Provides
    internal fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        throwOnMainThread("provideHttpLoggingInterceptor")
        return HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    }

    @Provides
    internal fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        throwOnMainThread("provideOkHttpClient")
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .socketFactory(DelegatingSocketFactory())
            .build()
    }
}
