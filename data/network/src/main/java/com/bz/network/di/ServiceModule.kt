package com.bz.network.di

import android.throwOnMainThread
import com.bz.network.api.service.CurrencyService
import com.bz.network.api.service.MovieService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Qualifier
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

private const val MOVIES_BASE_URL = "https://api.themoviedb.org/3/"
private const val CURRENCY_BASE_URL = "https://api.currencyapi.com/v3/"

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class MoviesRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class CurrencyRetrofit

@Module
@InstallIn(ViewModelComponent::class)
internal class ServiceModule {

    @Provides
    internal fun provideMoviesApiService(@MoviesRetrofit retrofit: Retrofit): MovieService {
        throwOnMainThread()
        return retrofit.create()
    }

    @Provides
    internal fun provideCurrencyApiService(@CurrencyRetrofit retrofit: Retrofit): CurrencyService {
        throwOnMainThread()
        return retrofit.create()
    }

    @Provides
    @MoviesRetrofit
    internal fun provideMoviesRetrofit(okHttpClient: OkHttpClient): Retrofit {
        throwOnMainThread()
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(MOVIES_BASE_URL)
            .build()
    }

    @Provides
    @CurrencyRetrofit
    internal fun provideCurrencyRetrofit(okHttpClient: OkHttpClient): Retrofit {
        throwOnMainThread()
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(CURRENCY_BASE_URL)
            .build()
    }
}
