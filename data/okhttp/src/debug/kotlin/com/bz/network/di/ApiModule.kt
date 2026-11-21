package com.bz.network.di

import com.bz.tools.DelegatingSocketFactory
import com.bz.tools.throwOnMainThread
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@Module
@InstallIn(SingletonComponent::class)
internal object ApiModule {
    @Singleton
    @Provides
    internal fun provideOkHttpClient(
        extraInterceptors: Set<@JvmSuppressWildcards Interceptor>
    ): OkHttpClient {
        throwOnMainThread()
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) }
            )
            .apply { extraInterceptors.forEach(::addInterceptor) }
            .socketFactory(DelegatingSocketFactory())
            .build()
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal interface InterceptorsModule {
    @Multibinds
    fun extraInterceptors(): Set<Interceptor>
}
