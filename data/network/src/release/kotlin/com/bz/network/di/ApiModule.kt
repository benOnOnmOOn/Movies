package com.bz.network.di

import com.bz.network.api.service.MovieService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import throwOnMainThread

const val BASE_URL = "https://api.themoviedb.org/3/"

@Module
@InstallIn(ViewModelComponent::class)
internal class ApiModule {

    @Provides
    internal fun provideOkHttpClient(): OkHttpClient {
        throwOnMainThread("provideOkHttpClient")
        return OkHttpClient.Builder()
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
}
