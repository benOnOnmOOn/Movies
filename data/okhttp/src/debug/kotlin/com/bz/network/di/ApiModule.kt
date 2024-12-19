package com.bz.network.di

import android.DelegatingSocketFactory
import com.bz.network.di.qualifiiers.CurrencyRetrofit
import com.bz.network.di.qualifiiers.MoviesRetrofit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import throwOnMainThread

internal const val MOVIES_BASE_URL = "https://api.themoviedb.org/3/"
internal const val CURRENCY_BASE_URL = "https://api.currencyapi.com/v3/"

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

    @Provides
    @MoviesRetrofit
    internal fun provideMoviesRetrofit(okHttpClient: OkHttpClient): Retrofit {
        throwOnMainThread("provideMoviesRetrofit")
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(MOVIES_BASE_URL)
            .build()
    }

    @Provides
    @CurrencyRetrofit
    internal fun provideCurrencyRetrofit(okHttpClient: OkHttpClient): Retrofit {
        throwOnMainThread("provideCurrencyRetrofit")
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(CURRENCY_BASE_URL)
            .build()
    }
}
