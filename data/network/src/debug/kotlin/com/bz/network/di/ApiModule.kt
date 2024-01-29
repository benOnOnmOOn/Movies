package com.bz.network.di

import android.os.Looper
import com.bz.network.api.service.MovieService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

const val BASE_URL = "https://api.themoviedb.org/3/"

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
            .build()
    }

    @Provides
    internal fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        throwOnMainThread("provideRetrofit")
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }

    @Provides
    internal fun provideApiService(retrofit: Retrofit): MovieService {
        throwOnMainThread("provideApiService")
        return retrofit.create()
    }

    private fun throwOnMainThread(methodName: String) {
        check(Looper.myLooper() != Looper.getMainLooper()) { "method: $methodName may not be called from main thread." }
    }
}
